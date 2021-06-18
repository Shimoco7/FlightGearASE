package ptm1;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class LinearRegPainter implements Painter{
    TimeSeries normalTs,anomalyTs;
    ArrayList<AnomalyReport> anomalyReports;


    @Override
    public void paint(XYChart chart, int timeStep, String selectedFeature) {

    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, ArrayList<AnomalyReport> ar) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = ar;
    }
}
