package ptm1;

import other.Properties;

import java.beans.XMLDecoder;
import java.io.*;
import java.util.ArrayList;

public class MainTrain {

    public static void main(String[] args) {

//
//        ArrayList<AnomalyReport> arr = new ArrayList<>();
//        ZscoreAnomalyDetector z = new ZscoreAnomalyDetector();
//        SimpleAnomalyDetector s = new SimpleAnomalyDetector();
//        TimeSeries train = new TimeSeries("./resources/reg_flight.csv");
//        TimeSeries test = new TimeSeries("./anomaly_flight.csv");
//        z.learnNormal(train);
//        arr = (ArrayList<AnomalyReport>) z.detect(test);
       // s.learnNormal(train);
        //arr = (ArrayList<AnomalyReport>) s.detect(test);
//
//        for(AnomalyReport a : arr){
//            System.out.println(a.description +" "+  a.timeStep);
//        }

        Properties p = new Properties();
        p.setDefaultProperties();
        p.createXML();
        XMLDecoder d = null;
        try {
            d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream("./resources/properties.xml")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties result = (Properties) d.readObject();
        d.close();


    }

}