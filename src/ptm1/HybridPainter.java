package ptm1;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class HybridPainter implements Painter{
    TimeSeries normalTs,anomalyTs;
    ArrayList<AnomalyReport> anomalyReports;

    @Override
    public void paint(LineChart chart,int oldTimeStep, int timeStep, String selectedFeature) {

    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, ArrayList<AnomalyReport> ar) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = ar;
    }



}
