package ptm1;

import javafx.scene.chart.LineChart;

import java.util.HashMap;
import java.util.HashSet;

public interface Painter {
    void paint(LineChart chart,int oldTimeStep, int timeStep, String selectedFeature);
    void setAll(TimeSeries normalTs, TimeSeries anomalyTs, HashMap<String, HashSet<Integer>> anomalies);
}
