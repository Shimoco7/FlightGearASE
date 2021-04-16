package test;

import java.util.List;

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector {


    @Override
    public void learnNormal(TimeSeries ts) {

    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        return null;
    }
}
