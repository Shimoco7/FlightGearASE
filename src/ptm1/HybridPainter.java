package ptm1;

import javafx.scene.chart.LineChart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HybridPainter implements Painter{
    TimeSeries normalTs,anomalyTs;
    HashMap<String, HashSet<Integer>> anomalyReports;

    @Override
    public void paint(LineChart chart,int oldTimeStep, int timeStep, String selectedFeature) {

    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, HashMap<String, HashSet<Integer>> anomalies) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = anomalies;
    }



}
