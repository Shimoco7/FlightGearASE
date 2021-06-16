package ptm1;

import java.util.List;

public interface TimeSeriesAnomalyDetector {
    void learnNormal(TimeSeries ts);
    List<AnomalyReport> detect(TimeSeries ts);
    void paint(int timeStep,String selectedFeature);
}