package ptm1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector {

	ArrayList<CorrelatedFeatures> corFeatures;
	ArrayList<Float> maxCorArray;
	LinkedHashMap<String, Float> zMap;
	LinkedHashMap<String, Circle> wMap;
	float[][] vals;
	int tempIndexSaver;

	public HybridAnomalyDetector() {
		corFeatures = new ArrayList<CorrelatedFeatures>();
		maxCorArray = new ArrayList<>();
		zMap = new LinkedHashMap<>();
		wMap = new LinkedHashMap<>();
		tempIndexSaver=0;
	}

	@Override
	// learning offline normal features and data
	public void learnNormal(TimeSeries ts) {
		ArrayList<String> ft = ts.getFeatures(); // variables
		int len = ts.getRowSize();
		int col2 = 0, col1 = 0;
		float correlation = 0;

		vals = new float[ft.size()][len]; // creating matrix from ts
		for (int i = 0; i < ft.size(); i++) {
			for (int j = 0; j < ts.getRowSize(); j++) {
				vals[i][j] = ts.getFeatureData(ft.get(i)).get(j);
			}
		}

		for (col1 = 0; col1 < ft.size(); col1++) {
			for (col2 = col1 + 1; col2 < ft.size(); col2++) {
				if(Math.abs(StatLib.pearson(vals[col1], vals[col2]))> correlation) {
					correlation = Math.abs(StatLib.pearson(vals[col1], vals[col2]));
					tempIndexSaver =col2;
				}
			}
			col2 =tempIndexSaver;
			if (correlation >= 0.95) { // The correlation is higher or equal to 0.95
				Point ps[] = toPoints(ts.getFeatureData(ft.get(col1)), ts.getFeatureData(ft.get(col2)));
				Line lin_reg = StatLib.linear_reg(ps); // Line Regression of the Correlated-Features
				float threshold = findThreshold(ps, lin_reg) * 1.1f; // 10% increase to cover normal points around
				CorrelatedFeatures c = new CorrelatedFeatures(ft.get(col1), ft.get(col2), correlation, lin_reg,
						threshold);
				corFeatures.add(c);
			} else if (correlation < 0.5) { // The correlation is less then 0.5
				float maxTh = 0, currAvg, currStd, currZscore;
				for (int i = 1; i < vals[col1].length; i++) {
					float[] arr = new float[i];

					for (int j = 0; j < i; j++) {
						arr[j] = vals[col1][j];
					}
					currAvg = StatLib.avg(arr);
					currStd = (float) Math.sqrt(StatLib.var(arr));
					currZscore = zScore(vals[col1][i], currAvg, currStd);
					maxTh = Math.max(currZscore, maxTh);
				}
				zMap.put(ft.get(col1), maxTh);
			} else { // The correlation is between 0.5 to 0.95
				WelzlMEC wMEC = new WelzlMEC();
				ArrayList<Point> ps = toPointsArrayList(ts.getFeatureData(ft.get(col1)),
						ts.getFeatureData(ft.get(col2)));
				Circle wCircle = wMEC.welzl(ps);
				wMap.put(ft.get(col1) + "," + ft.get(col2), wCircle);

			}

		}
	}

	@Override

	public List<AnomalyReport> detect(TimeSeries ts) { // Online detection of Anomalies during Time-Series input
		ArrayList<AnomalyReport> ar = new ArrayList<>();

		for (CorrelatedFeatures c : corFeatures) { // linear reg detect
			ArrayList<Float> x = ts.getFeatureData(c.feature1);
			ArrayList<Float> y = ts.getFeatureData(c.feature2);
			for (int i = 0; i < x.size(); i++) {
				// For each point of the correlated features, if the point deviation
				// from the line-regression exceeds the threshold it indicates an anomaly
				if (Math.abs(y.get(i) - c.lin_reg.f(x.get(i))) > c.threshold) {
					String d = c.feature1;
					// Time-steps in any given time series start from 1, thus k will be send to a
					// new Anomaly-Report as k+1
					ar.add(new AnomalyReport(d, (i + 1)));
				}
			}
		}

		ArrayList<String> zFeaturesNames = new ArrayList<String>(zMap.keySet()); // Zscore detect

		for (int i = 0; i < zMap.size(); i++) {
			ArrayList<Float> ftCol = ts.getFeatureData(zFeaturesNames.get(i));
			float currAvg, currStd, currZscore;

			for (int j = 1; j < ftCol.size(); j++) {
				float[] arr = new float[j];

				for (int k = 0; k < j; k++) {
					arr[k] = ftCol.get(k);
				}
				currAvg = StatLib.avg(arr);
				currStd = (float) Math.sqrt(StatLib.var(arr));
				currZscore = zScore(ftCol.get(j), currAvg, currStd);
				if (currZscore > zMap.get(i)) {
					ar.add(new AnomalyReport(zFeaturesNames.get(i), j + 1)); // ??
				}
			}
		}

		for (String s : wMap.keySet()) { // welzl detect
			String[] features = s.split(",");
			ArrayList<Float> col1Arr = ts.getFeatureData(features[0]);
			ArrayList<Float> col2Arr = ts.getFeatureData(features[1]);

			for (int i = 0; i < wMap.size(); i++) {
				WelzlMEC wMEC = new WelzlMEC();
				ArrayList<Point> ps = toPointsArrayList(col1Arr, col2Arr);
				Circle wCircle = wMEC.welzl(ps);
				if (wMap.get(s).isCircleInside(wCircle))
					ar.add(new AnomalyReport(features[0], i + 1)); // ??
			}
		}

		return ar;
	}

	private Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) {
		Point[] ps = new Point[x.size()];
		for (int i = 0; i < ps.length; i++)
			ps[i] = new Point(x.get(i), y.get(i));
		return ps;
	}

	private ArrayList<Point> toPointsArrayList(ArrayList<Float> x, ArrayList<Float> y) {
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

	public float zScore(float val, float avg, float stdev) {
		if (stdev == 0)
			return 0;
		return (Math.abs(val - avg) / stdev);
	}

	public List<CorrelatedFeatures> getNormalModel() {
		return corFeatures;
	}

}
