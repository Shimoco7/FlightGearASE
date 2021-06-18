package ptm1;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public interface Painter {
    void paint(XYChart chart, int timeStep, String selectedFeature);
    void setAll(TimeSeries normalTs, TimeSeries anomalyTs,ArrayList<AnomalyReport> ar);
}
