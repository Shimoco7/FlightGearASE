package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeSeries {

    private ArrayList<String> features;
    private Map<String, ArrayList<Float>> tsMap;
    private int dataRowSize;

    public TimeSeries(String csvFileName) {
        features = new ArrayList<String>();
        tsMap = new HashMap<String, ArrayList<Float>>();

        try {
            BufferedReader in=new BufferedReader(new FileReader(csvFileName));
            String line=in.readLine();
            for(String att : line.split(",")) {	//reads features-line
                features.add(att);
                tsMap.put(att, new ArrayList<>());
            }
            while((line=in.readLine())!=null) { //reads the entire data of the table
                int i=0;
                for(String val : line.split(",")) {
                    tsMap.get(features.get(i)).add(Float.parseFloat(val));
                    i++;
                }
            }
            dataRowSize=tsMap.get(features.get(0)).size();

            in.close();
        }catch(IOException e) {}
    }

    public ArrayList<Float> getFeatureData(String name){
        return tsMap.get(name);
    }

    public ArrayList<String> getFeatures(){
        return features;
    }

    public int getRowSize() {
        return dataRowSize;
    }

}