package model;

import javafx.beans.property.IntegerProperty;
import other.Properties;
import ptm1.Painter;
import ptm1.TimeSeries;

public interface Model {

    void setTimeSeries(TimeSeries ts);
    void setProperties(String path);
    void setTimeStep(IntegerProperty timeStep);
    <V> Properties getProperties();
    void play();
    void pause();
    void stop();
    void skipToStart();
    void skipToEnd();
    void fastForward();
    void slowForward();
    String uploadCsv(String nv);
    void setAnomalyDetector(String path);
    void close();
    Painter getPainter();

}
