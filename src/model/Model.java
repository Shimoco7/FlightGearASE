package model;

import javafx.beans.property.IntegerProperty;
import other.Properties;
import ptm1.TimeSeries;
import ptm1.TimeSeriesAnomalyDetector;

public interface Model {

    void start();
    void setTimeSeries(TimeSeries ts);
    void setProperties(String path);
    void setTimeStep(IntegerProperty timeStep);
    <V> Properties getProperties();
    void setAnomalyDetector(TimeSeriesAnomalyDetector ad);
    void play(int start, int rate);
    void skipToStart();
    void pause();
    void stop();
    String uploadCsv(String nv);
}
