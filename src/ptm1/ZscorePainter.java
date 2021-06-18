package ptm1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.*;
import other.Calculate;
import java.util.ArrayList;
import java.util.HashMap;

public class ZscorePainter implements Painter{
    TimeSeries normalTs,anomalyTs;
    ArrayList<AnomalyReport> anomalyReports;
    HashMap<String,ArrayList<Float>> zArrAnomalyMap;
    HashMap<String,Float> thresholdMap;
    XYChart.Series normalSeries;
    XYChart.Series anomalySeries;
    String currFeature;
    final CategoryAxis xAxis;
    final NumberAxis yAxis;

    public ZscorePainter() {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        currFeature="";
        normalSeries = new XYChart.Series();
        anomalySeries = new XYChart.Series();
    }

    @Override
    public void paint(LineChart chart,int oldTimeStep, int timeStep, String selectedFeature) {
        chart.setLegendVisible(false);

        if(!currFeature.equals(selectedFeature)){
            updateGraph(chart, timeStep, selectedFeature);
            currFeature = selectedFeature;
        }
        else{
            if(timeStep<=oldTimeStep){
                updateGraph(chart, timeStep, selectedFeature);
            }
            else {
                ObservableList<Float> points = FXCollections.observableArrayList(zArrAnomalyMap.get(selectedFeature).subList(oldTimeStep, timeStep));
                int len = points.size();
                int j = oldTimeStep;
                for (int i = 0; i < len; i++, j++) {
                    anomalySeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(j / 10), points.get(i)));
                }
            }
        }
    }

    private void updateGraph(LineChart chart, int timeStep, String selectedFeature) {
        normalSeries.getData().clear();
        anomalySeries.getData().clear();
        chart.getData().clear();
        Float threshold = thresholdMap.get(selectedFeature);
        int len = normalTs.getRowSize();
        for(int i=0;i<len;i++){
            normalSeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i/10),threshold));
        }
        chart.getData().add(normalSeries);
        Node node =chart.lookup(".chart-series-line");
        node.setStyle("-fx-stroke: grey");
        ObservableList<Float> points =  FXCollections.observableArrayList(zArrAnomalyMap.get(selectedFeature).subList(0,timeStep));
        for(int i=0;i<timeStep;i++){
            anomalySeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i/10),points.get(i)));
        }
        chart.getData().add(anomalySeries);
    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, ArrayList<AnomalyReport> ar) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = ar;
    }


}
