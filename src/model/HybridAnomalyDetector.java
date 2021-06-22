package model;

import other.*;

import java.util.*;

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector {
	
	//All-algorithms data members
	TimeSeries normalTs, anomalyTs;
	HashMap<String, HashSet<Integer>> anomalyReports;
	HashMap<String, Float> featureToCorl;
	private final HybridPainter painter;
	//LinearRegression algorithm data members
	ArrayList<CorrelatedFeatures> corFeatures;
	//Zscore algorithm data members
	LinkedHashMap<String, Float> zMap;
	HashMap<String,ArrayList<Float>> zArrAnomaly;
	//Welzl algorithm data member
	LinkedHashMap<String, Circle> wMap;

	public HybridAnomalyDetector() {
		corFeatures = new ArrayList<>();
		zMap = new LinkedHashMap<>();
		wMap = new LinkedHashMap<>();
		featureToCorl = new HashMap<>();
		zArrAnomaly = new HashMap<>();
		painter = new HybridPainter();
	}

	@Override
	// learning offline normal features and data with given TimeSeries
	public void learnNormal(TimeSeries ts) {
		this.normalTs = ts;
		ArrayList<String> ft = this.normalTs.getFeatures(); 

		for (String feature : ft) { 
			float corl = this.normalTs.getCorMap().get(feature).getCorVal();
			String corFeature = this.normalTs.getCorMap().get(feature).getCorFeature();
			featureToCorl.put(feature, corl);
			// If correlation is higher or equal to 0.95 we use Linear Regression Algorithm
			if (corl >= (float) 0.95) { 

				Point ps[] = toPoints(this.normalTs.getFeatureData(feature), this.normalTs.getFeatureData(corFeature));
				Line lin_reg = StatLib.linearReg(ps);
				 // 10% increase to cover normal points around
				float threshold = findThreshold(ps, lin_reg) * 1.1f;
				CorrelatedFeatures c = new CorrelatedFeatures(feature, corFeature, corl, lin_reg, threshold);
				corFeatures.add(c);
				// If correlation is less then 0.5 we use Zscore Algorithm
			} else if (corl < (float) 0.5) { 

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
				//If correlation is between 0.5 to 0.95 we use Welzl Algorithm
			} else { 
				ArrayList<Point> ps = toPointsArrayList(this.normalTs.getFeatureData(feature),
						this.normalTs.getFeatureData(corFeature));
				Circle wCircle = Welzl.makeCircle(ps);
				wMap.put(feature, wCircle);
			}

		}

	}
	// offline detection of Anomalies during Time-Series input
	@Override
	public void detect(TimeSeries ts) { 
		this.anomalyTs = ts;
		anomalyReports = new HashMap<>();
		// Linear Regression Algorithm detect stage
		for (CorrelatedFeatures c : corFeatures) { 
			ArrayList<Float> x = this.anomalyTs.getFeatureData(c.feature1);
			ArrayList<Float> y = this.anomalyTs.getFeatureData(c.feature2);
			for (int i = 0; i < x.size(); i++) {
				// For each point of the correlated features, if the point deviation
				// from the line-regression exceeds the threshold it is an anomaly
				if (Math.abs(y.get(i) - c.lineReg.f(x.get(i))) > c.threshold) {
					String d = c.feature1;

					if (!this.anomalyReports.containsKey(d))
						this.anomalyReports.put(d, new HashSet<>());
					this.anomalyReports.get(d).add(i);

				}
			}
		}
		// Zscore Algorithm detect stage
		ArrayList<String> zFeaturesNames = new ArrayList<String>(zMap.keySet()); 

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
				 if (!zArrAnomaly.containsKey(zFeaturesNames.get(i))) {
	                    zArrAnomaly.put(zFeaturesNames.get(i), new ArrayList<>());
	                }
	                zArrAnomaly.get(zFeaturesNames.get(i)).add(currZscore);
	                // if the current Zscore value is higher then one from the learnNormal stage it is an anomaly
				if (currZscore > zMap.get(zFeaturesNames.get(i))) {
					if (!this.anomalyReports.containsKey(zFeaturesNames.get(i)))
						this.anomalyReports.put(zFeaturesNames.get(i), new HashSet<>());
					this.anomalyReports.get(zFeaturesNames.get(i)).add(j);
				}
			}
		}
		// Welzl Algorithm detect stage
		for (String s : wMap.keySet()) { 
			String corFeature = this.normalTs.getCorMap().get(s).getCorFeature();
			ArrayList<Float> col1Arr = this.anomalyTs.getFeatureData(s);
			ArrayList<Float> col2Arr = this.anomalyTs.getFeatureData(corFeature);
			ArrayList<Point> ps = toPointsArrayList(col1Arr, col2Arr);
			for (int j = 0; j < ps.size(); j++) {
				// Checks if the specific point is contained in the Circle, if not it is an anomaly
				if (!wMap.get(s).isContained(ps.get(j))) { 
					if (!this.anomalyReports.containsKey(s))
						this.anomalyReports.put(s, new HashSet<>());
					this.anomalyReports.get(s).add(j); 
				}
			}
		}
		painter.anomalyReports = this.anomalyReports;
		painter.zArrAnomaly = this.zArrAnomaly;
		painter.zMap = this.zMap;
		painter.wMap = this.wMap;
		painter.featureToCorl = this.featureToCorl;
		painter.corFeatures = this.corFeatures;
	}
	// The function returns the Hybrid painter of HybridAnomalyDetector algorithm
	@Override
	public Painter getPainter() {
		if (normalTs != null && anomalyTs != null && anomalyReports != null)
			painter.setAll(normalTs, anomalyTs, anomalyReports);
		return painter;
	}
	// The function returns an array of points with x,y values
	private Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) { 
		Point[] ps = new Point[x.size()];
		for (int i = 0; i < ps.length; i++)
			ps[i] = new Point(x.get(i), y.get(i));
		return ps;
	}

	// The function returns an ArrayList of points with x,y values
	private ArrayList<Point> toPointsArrayList(ArrayList<Float> x, ArrayList<Float> y) { 
		ArrayList<Point> ps = new ArrayList<>();
		for (int i = 0; i < x.size(); i++)
			ps.add(new Point(x.get(i), y.get(i)));
		return ps;
	}

	// Maximum deviation of each point of the Correlation from the LinearRegression
	private float findThreshold(Point ps[], Line rl) {
		float max = 0;
		for (int i = 0; i < ps.length; i++) {
			float d = Math.abs(ps[i].y - rl.f(ps[i].x));
			if (d > max)
				max = d;
		}
		return max;
	}
	 // ZScore Calculator
	private float zScore(float val, float avg, float stdev) {
		if (stdev == 0)
			return 0;
		return (Math.abs(val - avg) / stdev);
	}
	// returns an ArrayList of CorrelatedFeatures
	public List<CorrelatedFeatures> getNormalModel() { 
		return corFeatures;
	}

}
