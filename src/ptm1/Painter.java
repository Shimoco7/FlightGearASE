package ptm1;

import javafx.scene.canvas.Canvas;

import java.util.ArrayList;

public interface Painter {
    void paint(Canvas canvas, int timeStep, String selectedFeature);
    void setAll(TimeSeries normalTs, TimeSeries anomalyTs,ArrayList<AnomalyReport> ar);
}
