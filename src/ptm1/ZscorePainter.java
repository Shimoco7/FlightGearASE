package ptm1;

import javafx.scene.Node;
import javafx.scene.chart.*;
import other.Calculate;

import java.util.ArrayList;

public class ZscorePainter implements Painter{
    TimeSeries normalTs,anomalyTs;
    ArrayList<AnomalyReport> anomalyReports;
    XYChart.Series normalSeries;
    XYChart.Series anomalySeries;
    final CategoryAxis xAxis;
    final NumberAxis yAxis;

    public ZscorePainter() {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
    }

    @Override
    public void paint(LineChart chart, int timeStep, String selectedFeature) {
        chart.setLegendVisible(false);
        if(normalSeries==null){
            normalSeries = new XYChart.Series();
            ArrayList<Float> normalPoints = normalTs.getFeatureData(selectedFeature);
            int len = normalPoints.size();
            for(int i=0;i<len;i++){
                normalSeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i/10),normalPoints.get(i)));
            }
            chart.getData().add(normalSeries);
            Node node =chart.lookup(".chart-series-line");
            node.setStyle("-fx-stroke: grey");
        }
    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, ArrayList<AnomalyReport> ar) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = ar;
    }

}
