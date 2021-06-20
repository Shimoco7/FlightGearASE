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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class HybridPainter implements Painter{
    HashMap<String,ArrayList<Float>> zArrAnomaly;
    ArrayList<CorrelatedFeatures> corFeatures;
    HashMap<String, Float> zMap;
    HashMap<String, Circle> wMap;
    TimeSeries normalTs,anomalyTs;
    HashMap<String, HashSet<Integer>> anomalyReports;
    HashMap<String, Float> featureToCurl;
    XYChart.Series normalSeries,anomalySeries, lineSeries,circleSeries, zNormalSeries,zAnomalySeries;
    LineChart myChart,myZscoreChart;
    boolean init,transformZ,transformRest;
    final NumberAxis xAxis,yAxis,zYaxis;
    final CategoryAxis zXaxis;
    String currFeature;

    public HybridPainter() {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        zYaxis = new NumberAxis();
        zXaxis = new CategoryAxis();

        normalSeries = new XYChart.Series();
        anomalySeries = new XYChart.Series();
        lineSeries = new XYChart.Series();
        circleSeries = new XYChart.Series();
        zNormalSeries = new XYChart.Series();
        zAnomalySeries = new XYChart.Series();

        myChart = new LineChart(xAxis,yAxis);
        myChart.setAnimated(false);
        myChart.setLegendVisible(false);

        myZscoreChart = new LineChart(zXaxis,yAxis);
        myZscoreChart.setAnimated(false);
        myZscoreChart.setCreateSymbols(false);
        myZscoreChart.setLegendVisible(false);

        currFeature = "";
    }

    @Override
    public void paint(StackPane pane, int oldTimeStep, int timeStep, String selectedFeature) {
        if(!init){
            pane.getChildren().remove(0,pane.getChildren().size());
            myChart.getData().clear();
            myZscoreChart.getData().clear();
            normalSeries.getData().clear();
            anomalySeries.getData().clear();
            lineSeries.getData().clear();
            circleSeries.getData().clear();
            zNormalSeries.getData().clear();
            zAnomalySeries.getData().clear();
            init=true;
        }

        float correlationDecider = featureToCurl.get(selectedFeature);

        //Linear-Regression Painter
        if(correlationDecider>0.95){
            paintLinearRegression(pane,oldTimeStep,timeStep,selectedFeature);
        }

        else if(correlationDecider<0.5){
            paintZscore(pane,oldTimeStep,timeStep,selectedFeature);
        }

        else{
            paintWelzl(pane,oldTimeStep,timeStep,selectedFeature);
        }

    }

    private void paintWelzl(StackPane pane, int oldTimeStep, int timeStep, String selectedFeature) {
        if(!transformRest){
            if(pane.getChildren().size()>0){
                pane.getChildren().remove(0,pane.getChildren().size());
            }
            myChart.getData().clear();
            normalSeries.getData().clear();
            anomalySeries.getData().clear();
            circleSeries.getData().clear();
            pane.getChildren().add(myChart);
            transformRest= true;
            transformZ= false;
        }

        if(!currFeature.equals(selectedFeature)){
            updateWelzlGraph(myChart, timeStep, selectedFeature);
            currFeature = selectedFeature;
        }
        else{
            if(timeStep<=oldTimeStep){
                updateWelzlGraph(myChart, timeStep, selectedFeature);
            }
            else {
                if(timeStep==oldTimeStep+1){
                    Float x=anomalyTs.getFeatureData(selectedFeature).get(timeStep);
                    Float y=anomalyTs.getFeatureData(normalTs.getCorMap().get(selectedFeature).getCorFeature()).get(timeStep);
                    anomalySeries.getData().add(new XYChart.Data<>(x,y));
                    if(timeStep>30){
                        anomalySeries.getData().remove(0);
                    }
                    checkAnomaly(timeStep,selectedFeature,normalTs.getCorMap().get(selectedFeature).getCorFeature());
                }
                else{
                    updateWelzlGraph(myChart, timeStep, selectedFeature);
                }
            }
        }

    }

    private void updateWelzlGraph(LineChart myChart, int timeStep, String selectedFeature) {
        normalSeries.getData().clear();
        anomalySeries.getData().clear();
        circleSeries.getData().clear();
        myChart.getData().clear();

        String correlatedFeature = normalTs.getCorMap().get(selectedFeature).getCorFeature();
        Circle circle = wMap.get(selectedFeature);
        for(int i=0;i<1000;i++){
            double angle = Math.random()*Math.PI*2;
            double x = Math.cos(angle) *circle.getRadius() +circle.getPoint().x;
            double y = Math.sin(angle) *circle.getRadius()+circle.getPoint().y;
            circleSeries.getData().add(new XYChart.Data<>(x,y));
        }
        myChart.getData().add(circleSeries);

        Node node =myChart.lookup(".series0.chart-series-line");
        node.setStyle("-fx-stroke: transparent;");

        ArrayList<Float> xValues = normalTs.getFeatureData(selectedFeature);
        ArrayList<Float> yValues = normalTs.getFeatureData(correlatedFeature);
        int len=xValues.size();
        for(int i=0;i<len;i++){
            normalSeries.getData().add(new XYChart.Data<>(xValues.get(i),yValues.get(i)));
        }
        myChart.getData().add(normalSeries);

        ObservableList<Float> pointsX,pointsY;
        if(timeStep>30){
            pointsX = FXCollections.observableArrayList(anomalyTs.getFeatureData(selectedFeature).subList(timeStep-30,timeStep));
            pointsY = FXCollections.observableArrayList(anomalyTs.getFeatureData(correlatedFeature).subList(timeStep-30,timeStep));
        }
        else{
            pointsX = FXCollections.observableArrayList(anomalyTs.getFeatureData(selectedFeature).subList(0,timeStep));
            pointsY = FXCollections.observableArrayList(anomalyTs.getFeatureData(correlatedFeature).subList(0,timeStep));
        }
        len=pointsX.size();
        for(int i=0;i<len;i++){
            anomalySeries.getData().add(new XYChart.Data<>(pointsX.get(i),pointsY.get(i)));
        }
        myChart.getData().add(anomalySeries);

        checkAnomaly(timeStep,selectedFeature,correlatedFeature);

    }


    private void paintZscore(StackPane pane, int oldTimeStep, int timeStep, String selectedFeature) {
        if(!transformZ){
            if(pane.getChildren().size()>0){
                pane.getChildren().remove(0,pane.getChildren().size());
            }
            myZscoreChart.getData().clear();
            zNormalSeries.getData().clear();
            zAnomalySeries.getData().clear();
            pane.getChildren().add(myZscoreChart);
            transformZ= true;
            transformRest=false;
        }

        if(!currFeature.equals(selectedFeature)){
            updatezScoreGraph(myZscoreChart, timeStep, selectedFeature);
            currFeature = selectedFeature;
        }
        else{
            if(timeStep<=oldTimeStep){
                updatezScoreGraph(myZscoreChart, timeStep, selectedFeature);
            }
            else {
                ObservableList<Float> points = FXCollections.observableArrayList(zArrAnomaly.get(selectedFeature).subList(oldTimeStep, timeStep));
                int len = points.size();
                int j = oldTimeStep;
                for (int i = 0; i < len; i++, j++) {
                    zAnomalySeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(j / 10), points.get(i)));
                }
                checkZscoreAnomaly(timeStep, selectedFeature);
            }
        }
    }

    private void updatezScoreGraph(LineChart myZscoreChart, int timeStep, String selectedFeature) {
        zNormalSeries.getData().clear();
        zAnomalySeries.getData().clear();
        myZscoreChart.getData().clear();
        Float threshold = zMap.get(selectedFeature);
        int len = normalTs.getRowSize();
        for(int i=0;i<len;i++){
            zNormalSeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i/10),threshold));
        }
        myZscoreChart.getData().add(zNormalSeries);
        Node node =myZscoreChart.lookup(".chart-series-line");
        node.setStyle("-fx-stroke: grey");
        ObservableList<Float> points =  FXCollections.observableArrayList(zArrAnomaly.get(selectedFeature).subList(0,timeStep));
        for(int i=0;i<timeStep;i++){
            zAnomalySeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i/10),points.get(i)));
        }
        myZscoreChart.getData().add(zAnomalySeries);

        checkZscoreAnomaly(timeStep, selectedFeature);
    }

    private void checkZscoreAnomaly(int timeStep, String selectedFeature) {
        if(anomalyReports.containsKey(selectedFeature)){
            if(anomalyReports.get(selectedFeature).contains(timeStep)){
                ApplicationStatus.setAppColor(Color.BLACK);
                ApplicationStatus.setAppFillColor("red");
                ApplicationStatus.setAppStatusValue("Anomaly has been detected in "+selectedFeature+" feature, at "+ Calculate.getTimeString(timeStep/10));
                ApplicationStatus.pausePlayFromStart();
            }
        }
    }

    private void paintLinearRegression(StackPane pane, int oldTimeStep, int timeStep, String selectedFeature) {
        if(!transformRest){
            if(pane.getChildren().size()>0){
                pane.getChildren().remove(0,pane.getChildren().size());
            }
            myChart.getData().clear();
            normalSeries.getData().clear();
            anomalySeries.getData().clear();
            lineSeries.getData().clear();
            pane.getChildren().add(myChart);
            transformRest= true;
            transformZ= false;
        }

        String correlatedFeature = null;
        Line line = null;
        for(CorrelatedFeatures c : corFeatures){
            if(c.feature1.equals(selectedFeature)){
                correlatedFeature=c.feature2;
                line = c.lin_reg;
            }
        }

        if(!currFeature.equals(selectedFeature)){
            updateLinearRegGraph(timeStep, selectedFeature, correlatedFeature, line);
            currFeature = selectedFeature;
        }

        else{
            if(timeStep<=oldTimeStep){
                updateLinearRegGraph(timeStep, selectedFeature, correlatedFeature, line);
            }

            else{
                if(timeStep==oldTimeStep+1){
                    Float x=anomalyTs.getFeatureData(selectedFeature).get(timeStep);
                    Float y=anomalyTs.getFeatureData(correlatedFeature).get(timeStep);
                    anomalySeries.getData().add(new XYChart.Data<>(x,y));
                    if(timeStep>30){
                        anomalySeries.getData().remove(0);
                    }
                    checkAnomaly(timeStep,selectedFeature,correlatedFeature);
                }
                else{
                    updateLinearRegGraph(timeStep, selectedFeature, correlatedFeature, line);
                }
            }
        }
    }

    private void updateLinearRegGraph(int timeStep, String selectedFeature, String correlatedFeature, Line line) {
        myChart.getData().clear();
        normalSeries.getData().clear();
        anomalySeries.getData().clear();
        lineSeries.getData().clear();

        ArrayList<Float> xValues = normalTs.getFeatureData(selectedFeature);
        ArrayList<Float> yValues = normalTs.getFeatureData(correlatedFeature);
        int len=xValues.size();
        for(int i=0;i<len;i++){
            normalSeries.getData().add(new XYChart.Data<>(xValues.get(i),yValues.get(i)));
        }
        myChart.getData().add(normalSeries);

        ObservableList<Float> pointsX,pointsY;
        if(timeStep>30){
            pointsX = FXCollections.observableArrayList(anomalyTs.getFeatureData(selectedFeature).subList(timeStep-30,timeStep));
            pointsY = FXCollections.observableArrayList(anomalyTs.getFeatureData(correlatedFeature).subList(timeStep-30,timeStep));

        }
        else{
            pointsX = FXCollections.observableArrayList(anomalyTs.getFeatureData(selectedFeature).subList(0,timeStep));
            pointsY = FXCollections.observableArrayList(anomalyTs.getFeatureData(correlatedFeature).subList(0,timeStep));
        }
        len=pointsX.size();
        for(int i=0;i<len;i++){
            anomalySeries.getData().add(new XYChart.Data<>(pointsX.get(i),pointsY.get(i)));
        }
        myChart.getData().add(anomalySeries);

        Float max = Collections.max(normalTs.getFeatureData(selectedFeature));
        Float min = Collections.min(normalTs.getFeatureData(selectedFeature));
        lineSeries.getData().add(new XYChart.Data<>(min,line.f(min)));
        lineSeries.getData().add(new XYChart.Data<>(max,line.f(max)));
        myChart.getData().add(lineSeries);

        Node node =myChart.lookup(".series0.chart-series-line");
        node.setStyle("-fx-stroke: transparent;");

        Node node1 =myChart.lookup(".series1.chart-series-line");
        node1.setStyle("-fx-stroke: transparent;");

        Node node2=myChart.lookup(".series2.chart-series-line");
        node2.setStyle("-fx-stroke: grey;");

        checkAnomaly(timeStep,selectedFeature,correlatedFeature);
    }

    private void checkAnomaly(int timeStep, String selectedFeature, String correlatedFeature) {
        if(anomalyReports.containsKey(selectedFeature)){
            if(anomalyReports.get(selectedFeature).contains(timeStep)){
                ApplicationStatus.setAppColor(Color.BLACK);
                ApplicationStatus.setAppFillColor("red");
                ApplicationStatus.setAppStatusValue("Anomaly has been detected in ["+selectedFeature+"   ,   " + correlatedFeature+"] at " + Calculate.getTimeString(timeStep/10));
                ApplicationStatus.pausePlayFromStart();
            }
        }
    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, HashMap<String, HashSet<Integer>> anomalies) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = anomalies;
    }


}
