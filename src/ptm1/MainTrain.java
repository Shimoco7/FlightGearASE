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
//        Random r=new Random();
//        int port=6000+r.nextInt(1000);
//        Server server=new Server();
//        server.start(port, new AnomalyDetectionHandler());
//        testClient(port);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {}
//        server.stop();
//        check("output.txt", "expectedOutput.txt");
//        System.out.println("done");
//
//        ArrayList<AnomalyReport> arr = new ArrayList<>();
//        //  ZscoreAnomalyDetector z = new ZscoreAnomalyDetector();
//        // SimpleAnomalyDetector s = new SimpleAnomalyDetector();
//        HybridAnomalyDetector h = new HybridAnomalyDetector();
//        TimeSeries train = new TimeSeries("./resources/reg_flight.csv");
//        System.out.println(train.getCorMap());
//
//        TimeSeries test = new TimeSeries("./resources/anomaly_flight.csv");
////        z.learnNormal(train);
////        arr = (ArrayList<AnomalyReport>) z.detect(test);
//        // s.learnNormal(train);
//        //arr = (ArrayList<AnomalyReport>) s.detect(test);
//        h.learnNormal(train);
//        arr = (ArrayList<AnomalyReport>)h.detect(test);
//        for(AnomalyReport a : arr){
//            System.out.println(a.description +" "+  a.timeStep);
//        }
        System.out.println(Calculate.getTimeString(152));



    }

}