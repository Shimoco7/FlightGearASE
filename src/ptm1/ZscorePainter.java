package ptm1;

import javafx.scene.chart.XYChart;
import other.Calculate;

import java.util.ArrayList;

public class ZscorePainter implements Painter{
    TimeSeries normalTs,anomalyTs;
    ArrayList<AnomalyReport> anomalyReports;
    XYChart.Series normalSeries;

    public ZscorePainter() {
        normalSeries = new XYChart.Series();
    }

    @Override
    public void paint(XYChart chart, int timeStep, String selectedFeature) {
        ArrayList<Float> normalPoints = normalTs.getFeatureData(selectedFeature);
        int len = normalPoints.size();
        for(int i=0;i<len;i++){
            normalSeries.getData().add(new XYChart.Data<>(Calculate.getTimeString(i),normalPoints.get(i)));
        }
    }

    @Override
    public void setAll(TimeSeries normalTs, TimeSeries anomalyTs, ArrayList<AnomalyReport> ar) {
        this.normalTs = normalTs;
        this.anomalyTs = anomalyTs;
        this.anomalyReports = ar;
    }
}
