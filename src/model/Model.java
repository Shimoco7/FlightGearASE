package model;

import ptm1.TimeSeries;
import ptm1.TimeSeriesAnomalyDetector;

public interface Model {

    void start();
    void setTimeSeries(TimeSeries ts);
    void setProperties(String path);
    void setAnomalyDetector(TimeSeriesAnomalyDetector ad);
    void play(int start, int rate);
    void pause();
    void stop();
}
