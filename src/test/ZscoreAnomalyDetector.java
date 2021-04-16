package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sun.security.timestamp.TSRequest;

public class ZscoreAnomalyDetector implements TimeSeriesAnomalyDetector {
	
	
	  HashMap<String, Float> map = new HashMap<>();
	  Float max;
	

    public ZscoreAnomalyDetector() {
		super();
	}

	@Override
    public void learnNormal(TimeSeries ts) { 
		float avg =0;
		double s = 0;
        ArrayList<String> ft=ts.getFeatures();
        
        for(String string: ft) {
        	float arrFloat[]=toFloat(ts.getFeatureData(string));
        	for(int i=0; i<arrFloat.length; i++)
             	max = (float) Math.max(max, zscore(arrFloat[i], arrFloat)) ;
        	map.put(string, max );
        } 
        
          
    }

    public static float zscore(float f,float[] arrFloat){
    	
    	Float max=(float) 0;
    	float avg = StatLib.avg(arrFloat);
        double s =  Math.sqrt(StatLib.var(arrFloat));
        
        return (float) ( Math.abs(f-avg)/s) ;

    }
    
    
    public static float[] toFloat(ArrayList<Float> arr){
    	
    	  float arrFloat[]=new float[arr.size()];
          for(int i=0; i<arr.size(); i++)
          	arrFloat[i]= arr.get(i).floatValue();
        
        return arrFloat;
    }

	@Override
    public List<AnomalyReport> detect(TimeSeries ts) {
		
		  ArrayList<AnomalyReport> ar=new ArrayList<>();
		  ArrayList<String> ft=ts.getFeatures();
	        for(String string : ft) {
	            float arrFloat[]=toFloat(ts.getFeatureData(string));
	            for(int i=0; i<arrFloat.length; i++)
	            {
	            	max = zscore(arrFloat[i], arrFloat);
	            	if(max>map.get(string).floatValue()){
	                    ar.add(new AnomalyReport(string,(i+1)));
	                }
	            }
	        }
	        return ar;
    }
}
