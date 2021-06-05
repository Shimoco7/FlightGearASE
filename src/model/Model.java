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
    void play();
    void pause();
    void stop();
    void skipToStart();
    void skipToEnd();
    void fastForward();
    void slowForward();
    String uploadCsv(String nv);
}
