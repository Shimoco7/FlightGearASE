package ptm1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import other.Calculate;
import view.ApplicationStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LinearRegPainter implements Painter{
    LineChart myChart;
    TimeSeries normalTs,anomalyTs;
    HashMap<String, HashSet<Integer>> anomalyReports;
    ArrayList<CorrelatedFeatures> corFeatures;
    boolean init;
    XYChart.Series normalSeries,anomalySeries, lineSeries;
    String currFeature;
    final NumberAxis xAxis;
    final NumberAxis yAxis;

    public LinearRegPainter() {
       xAxis = new NumberAxis();
       yAxis = new NumberAxis();
       normalSeries = new XYChart.Series();
       anomalySeries = new XYChart.Series();
       lineSeries = new XYChart.Series();
       myChart = new LineChart(xAxis,yAxis);
       currFeature = "";
    }

    @Override
    public void paint(StackPane pane, int oldTimeStep, int timeStep, String selectedFeature) {
        myChart.setLegendVisible(false);
        if(!init){
            pane.getChildren().remove(0,pane.getChildren().size());
            myChart.getData().clear();
            pane.getChildren().add(myChart);
            init=true;
        }
        boolean flag = false;
        String correlatedFeature = null;
        Line line = null;
        for(CorrelatedFeatures c : corFeatures){
            if(c.feature1.equals(selectedFeature)){
                correlatedFeature=c.feature2;
                line = c.lin_reg;
                flag = true;
            }
        }
        if(!flag){
            ApplicationStatus.setAppColor(Color.BLACK);
            ApplicationStatus.setAppFillColor("orange");
            ApplicationStatus.setAppStatusValue(selectedFeature + " does not have a correlated feature that fits the Linear-Regression Algorithm");
            return;
        }
        else{
            ApplicationStatus.setAppStatusValue("");
            ApplicationStatus.setAppFillColor("transparent");
        }

        if(!currFeature.equals(selectedFeature)){
            normalSeries.getData().clear();
            anomalySeries.getData().clear();
            lineSeries.getData().clear();
            myChart.getData().clear();
            lineSeries.getData().add(new XYChart.Data<>(0,line.f(0)));
            Float max = getMaxElement(normalTs.getFeatureData(selectedFeature));
            lineSeries.getData().add(new XYChart.Data<>(max,line.f(max)));
            myChart.getData().add(lineSeries);
            
            ArrayList<Float> xValues = normalTs.getFeatureData(selectedFeature);
            ArrayList<Float> yValues = normalTs.getFeatureData(correlatedFeature);
            int len=xValues.size();
            for(int i=0;i<len;i++){
                normalSeries.getData().add(new XYChart.Data<>(xValues.get(i),yValues.get(i)));
            }
            myChart.getData().add(normalSeries);
            Node node =myChart.lookup(".series0.chart-series-line");
            node.setStyle("-fx-stroke: grey;");

            Node node1 =myChart.lookup(".series1.chart-series-line");
            node1.setStyle("-fx-stroke: transparent;");

            currFeature = selectedFeature;
        }
        else{
            if(timeStep<=oldTimeStep){
                
            }
            
            else{
                
            }
        }


//        if(!currFeature.equals(selectedFeature)){
//            updateGraph(chart, timeStep, selectedFeature);
//            currFeature = selectedFeature;
//        }
//        else{
//            if(timeStep<=oldTimeStep){
//                updateGraph(chart, timeStep, selectedFeature);
//            }
//            else {
//                ObservableList<Float> points = FXCollections.observableArrayList(zArrAnomalyMap.get(selectedFeature).subList(oldTimeStep, timeStep));
//                int len = points.size();
//                int j = oldTimeStep;
//                for (int i = 0; i < len; i++, j++) {
//                    anomalySeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(j / 10), points.get(i)));
//                }
//                checkAnomaly(timeStep, selectedFeature);
//            }
//        }
    }

    private Float getMaxElement(ArrayList<Float> featureData) {
        Float max = featureData.get(0);
        for(int i=1;i<featureData.size();i++){
            if(featureData.get(i)>max){
                max = featureData.get(i);
            }
        }
        return max;
    }


    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, HashMap<String, HashSet<Integer>> anomalies) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = anomalies;
    }

    private void updateGraph(LineChart chart, int timeStep, String selectedFeature) {
//        normalSeries.getData().clear();
//        anomalySeries.getData().clear();
//        chart.getData().clear();
//        Float threshold = thresholdMap.get(selectedFeature);
//        int len = normalTs.getRowSize();
//        for(int i=0;i<len;i++){
//            normalSeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i/10),threshold));
//        }
//        chart.getData().add(normalSeries);
//        Node node =chart.lookup(".chart-series-line");
//        node.setStyle("-fx-stroke: grey");
//        ObservableList<Float> points =  FXCollections.observableArrayList(zArrAnomalyMap.get(selectedFeature).subList(0,timeStep));
//        for(int i=0;i<timeStep;i++){
//            anomalySeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i/10),points.get(i)));
//        }
//        chart.getData().add(anomalySeries);
//
//        checkAnomaly(timeStep, selectedFeature);
    }

}
