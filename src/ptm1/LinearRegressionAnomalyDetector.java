package ptm1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class LinearRegressionAnomalyDetector implements TimeSeriesAnomalyDetector {

	ArrayList<CorrelatedFeatures> corFeatures;
	float corlThreshold;
	TimeSeries normalTs, anomalyTs;
	HashMap<String, HashSet<Integer>> anomalyReports;
	private final LinearRegPainter painter;

	public LinearRegressionAnomalyDetector() {
		corFeatures = new ArrayList<CorrelatedFeatures>();
		corlThreshold = (float) 0.9;
		painter = new LinearRegPainter();
	}

	@Override
	// learning offline normal features and data
	public void learnNormal(TimeSeries ts) {
		this.normalTs = ts;
		ArrayList<String> ft = this.normalTs.getFeatures();

		for (String feature : ft) {
			float corl = this.normalTs.getCorMap().get(feature).getCorVal();
			String corFeature = this.normalTs.getCorMap().get(feature).getcorFeature();

			if (corl > corlThreshold) { // Strong Correlation between the
										// features

				Point ps[] = toPoints(this.normalTs.getFeatureData(feature), this.normalTs.getFeatureData(corFeature));
				Line lin_reg = StatLib.linear_reg(ps); // Line Regression of the Correlated-Features
				float threshold = findThreshold(ps, lin_reg) * 1.1f; // 10% increase to cover normal points around the
																		// normal area

				CorrelatedFeatures c = new CorrelatedFeatures(feature, corFeature, corl, lin_reg, threshold);

				corFeatures.add(c);

			}
		}
	}

	private Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) {
		Point[] ps = new Point[x.size()];
		for (int i = 0; i < ps.length; i++)
			ps[i] = new Point(x.get(i), y.get(i));
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

	@Override
	// Online detection of Anomalies during Time-Series input
	public void detect(TimeSeries ts) {
		this.anomalyTs = ts;
		this.anomalyReports = new HashMap<>();

		for (CorrelatedFeatures c : corFeatures) {
			ArrayList<Float> x = this.anomalyTs.getFeatureData(c.feature1);
			ArrayList<Float> y = this.anomalyTs.getFeatureData(c.feature2);
			for (int i = 0; i < x.size(); i++) {
				// For each point of the correlated features, if the point deviation
				// from the line-regression exceeds the threshold it indicates an anomaly
				if (Math.abs(y.get(i) - c.lin_reg.f(x.get(i))) > c.threshold) {
					String d = c.feature1;
					// Time-steps in any given time series start from 1, thus k will be send to a
					// new Anomaly-Report as i

					if (!this.anomalyReports.containsKey(d))
						this.anomalyReports.put(d, new HashSet<>());
					this.anomalyReports.get(d).add(i);
				}
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

	public List<CorrelatedFeatures> getNormalModel() {
		return corFeatures;
	}

	public float getCorlThreshold() {
		return corlThreshold;
	}

	public void setCorlThreshold(float corlThreshold) {
		this.corlThreshold = corlThreshold;
	}

}
