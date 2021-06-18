package ptm1;

import other.Calculate;
import other.Properties;

import java.beans.XMLDecoder;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

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
        for(Float f  : z.zArr){
            System.out.println(f);
        }



    }

}