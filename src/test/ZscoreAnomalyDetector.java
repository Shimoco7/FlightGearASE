package test;

import java.util.List;

public class ZscoreAnomalyDetector implements TimeSeriesAnomalyDetector {


    @Override
    public void learnNormal(TimeSeries ts) {

    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        return null;
    }
}
