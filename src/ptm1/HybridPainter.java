package ptm1;

import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class HybridPainter implements Painter{
    //HashMap<String,ArrayList<Float>> zArrAnomalyMap
    //HashMap<String,Float> thresholdMap = ZMAP
    //ArrayList<CorrelatedFeatures> corFeatures;
    //LinkedHashMap<String, Float> zMap; // for Zscore
    //LinkedHashMap<String, Circle> wMap; // for Welzl
    TimeSeries normalTs,anomalyTs;
    HashMap<String, HashSet<Integer>> anomalyReports;

    @Override
    public void paint(StackPane stackPane, int oldTimeStep, int timeStep, String selectedFeature) {

    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, HashMap<String, HashSet<Integer>> anomalies) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = anomalies;
    }



}
