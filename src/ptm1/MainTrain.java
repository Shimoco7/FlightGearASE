package ptm1;

import model.TimeSeries;
import model.ZscoreAnomalyDetector;
import other.AnomalyReport;

import java.util.ArrayList;

public class MainTrain {

    public static void main(String[] args) {

        ArrayList<AnomalyReport> arr = new ArrayList<>();
          ZscoreAnomalyDetector z = new ZscoreAnomalyDetector();
        // SimpleAnomalyDetector s = new SimpleAnomalyDetector();
        //HybridAnomalyDetector h = new HybridAnomalyDetector();
        TimeSeries train = new TimeSeries("./resources/reg_flight.csv");
        TimeSeries test = new TimeSeries("./resources/anomaly_flight.csv");
        z.learnNormal(train);
        //arr = (ArrayList<AnomalyReport>) z.detect(test);
        // s.learnNormal(train);
        //arr = (ArrayList<AnomalyReport>) s.detect(test);
        //h.learnNormal(train);
        //arr = (ArrayList<AnomalyReport>)h.detect(test);




    }

}