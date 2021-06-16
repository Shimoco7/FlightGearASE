package ptm1;

import java.util.ArrayList;

public interface Painter {
    void paint(int timeStep,String selectedFeature);
    void setAll(TimeSeries normalTs, TimeSeries anomalyTs,ArrayList<AnomalyReport> ar);
}
