package ptm1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector {

	ArrayList<CorrelatedFeatures> corFeatures; // for Linear Regression
	LinkedHashMap<String, Float> zMap; // for Zscore
	LinkedHashMap<String, Circle> wMap; // for Welzl
	TimeSeries normalTs, anomalyTs;
	HashMap<String, HashSet<Integer>> anomalyReports;
	private final HybridPainter painter;

	public HybridAnomalyDetector() {
		corFeatures = new ArrayList<>();
		zMap = new LinkedHashMap<>();
		wMap = new LinkedHashMap<>();
		painter = new HybridPainter();
	}

	@Override
	// learning offline normal features and data
	public void learnNormal(TimeSeries ts) {
		this.normalTs = ts;
		ArrayList<String> ft = this.normalTs.getFeatures(); // learnNormal variables

		for (String feature : ft) { // using different algorithm by different correlation
			float corl = this.normalTs.getCorMap().get(feature).getCorVal();
			String corFeature = this.normalTs.getCorMap().get(feature).getcorFeature();
			if (corl >= (float) 0.95) { // The correlation is higher or equal to 0.95 -> Linear Regression Algorithm
										// learn

				Point ps[] = toPoints(this.normalTs.getFeatureData(feature), this.normalTs.getFeatureData(corFeature));
				Line lin_reg = StatLib.linear_reg(ps); // Linear Regression of the Correlated-Features
				float threshold = findThreshold(ps, lin_reg) * 1.1f; // 10% increase to cover normal points around
				CorrelatedFeatures c = new CorrelatedFeatures(feature, corFeature, corl, lin_reg, threshold);
				corFeatures.add(c);
			} else if (corl < (float) 0.5) { // The correlation is less then 0.5 -> Zscore Algorithm learn

				float maxTh = 0, currAvg, currStd, currZscore;
				for (int i = 1; i < this.normalTs.getFeatureData(feature).size(); i++) {
					float[] arr = new float[i];

					for (int j = 0; j < i; j++) {
						arr[j] = this.normalTs.getFeatureData(feature).get(j);
					}
					currAvg = StatLib.avg(arr);
					currStd = (float) Math.sqrt(StatLib.var(arr));
					currZscore = zScore(this.normalTs.getFeatureData(feature).get(i), currAvg, currStd);
					maxTh = Math.max(currZscore, maxTh);
				}
				zMap.put(feature, maxTh);
			} else { // The correlation is between 0.5 to 0.95 -> Welzl Algorithm learn
				ArrayList<Point> ps = toPointsArrayList(this.normalTs.getFeatureData(feature),
						this.normalTs.getFeatureData(corFeature));
				Circle wCircle = Welzl.makeCircle(ps);
				wMap.put(feature + "," + corFeature, wCircle);

			}

		}

	}

	@Override

	public void detect(TimeSeries ts) { // Online detection of Anomalies during Time-Series input
		this.anomalyTs = ts;
		anomalyReports = new HashMap<>();
		anomalyReports.keySet().addAll(this.anomalyTs.getFeatures());

		for (CorrelatedFeatures c : corFeatures) { // Linear Regression Algorithm detect
			ArrayList<Float> x = this.anomalyTs.getFeatureData(c.feature1);
			ArrayList<Float> y = this.anomalyTs.getFeatureData(c.feature2);
			for (int i = 0; i < x.size(); i++) {
				// For each point of the correlated features, if the point deviation
				// from the line-regression exceeds the threshold it indicates an anomaly
				if (Math.abs(y.get(i) - c.lin_reg.f(x.get(i))) > c.threshold) {
					String d = c.feature1;
					// Time-steps in any given time series start from 1, thus k will be send to a
					// new Anomaly-Report as k+1

					if (this.anomalyReports.get(d) == null)
						this.anomalyReports.put(d, new HashSet<>());
					this.anomalyReports.get(d).add(i);

				}
			}
		}

		ArrayList<String> zFeaturesNames = new ArrayList<String>(zMap.keySet()); // Zscore Algorithm detect

		for (int i = 0; i < zMap.size(); i++) {
			ArrayList<Float> ftCol = this.anomalyTs.getFeatureData(zFeaturesNames.get(i));
			float currAvg, currStd, currZscore;

			for (int j = 1; j < ftCol.size(); j++) {
				float[] arr = new float[j];

				for (int k = 0; k < j; k++) {
					arr[k] = ftCol.get(k);
				}
				currAvg = StatLib.avg(arr);
				currStd = (float) Math.sqrt(StatLib.var(arr));
				currZscore = zScore(ftCol.get(j), currAvg, currStd);
				if (currZscore > zMap.get(zFeaturesNames.get(i))) { // if the current Zscore is higher then Zscore from
					if (this.anomalyReports.get(zFeaturesNames.get(i)) == null)
						this.anomalyReports.put(zFeaturesNames.get(i), new HashSet<>());
					this.anomalyReports.get(zFeaturesNames.get(i)).add(j); // the learnNormal// ??
				}
			}
		}
		for (String s : wMap.keySet()) { // Welzl Algorithm detect
			String[] features = s.split(",");
			ArrayList<Float> col1Arr = this.anomalyTs.getFeatureData(features[0]);
			ArrayList<Float> col2Arr = this.anomalyTs.getFeatureData(features[1]);
			ArrayList<Point> ps = toPointsArrayList(col1Arr, col2Arr);
			// Circle wCircle = wMEC.welzl(ps);
			for (int j = 0; j < ps.size(); j++) {
				if (!wMap.get(s).isContained(ps.get(j))) // Checks if the point is contained in the Circle from the
					if (this.anomalyReports.get(features[0]) == null)
						this.anomalyReports.put(features[0], new HashSet<>());
					this.anomalyReports.get(features[0]).add(j);							// learnNormal
			}
		}
		painter.anomalyReports = this.anomalyReports;
	}

	@Override
	public Painter getPainter() {
		if (normalTs != null && anomalyTs != null && anomalyReports != null)
			painter.setAll(normalTs, anomalyTs, anomalyReports);
		return painter;
	}

	private Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) { // The function returns an array of points by
																		// getting 2 ArrayLists of Floats
		Point[] ps = new Point[x.size()];
		for (int i = 0; i < ps.length; i++)
			ps[i] = new Point(x.get(i), y.get(i));
		return ps;
	}

	private ArrayList<Point> toPointsArrayList(ArrayList<Float> x, ArrayList<Float> y) { // The function returns an
																							// ArrayList of points by
																							// getting 2 arrayList of
																							// Floats
		ArrayList<Point> ps = new ArrayList<>();
		for (int i = 0; i < x.size(); i++)
			ps.add(new Point(x.get(i), y.get(i)));
		return ps;
	}

	// Maximum deviation of each point of the Correlation from the Line-regression
	private float findThreshold(Point ps[], Line rl) {
		float max = 0;
		for (int i = 0; i < ps.length; i++) {
			float d = Math.abs(ps[i].y - rl.f(ps[i].x));
			if (d > max)
				max = d;
		}
		return max;
	}

	public float zScore(float val, float avg, float stdev) { // ZScore calculation
		if (stdev == 0)
			return 0;
		return (Math.abs(val - avg) / stdev);
	}

	public List<CorrelatedFeatures> getNormalModel() { // returns an ArrayList of CorrelatedFeatures
		return corFeatures;
	}

}
