package Model;

import java.util.ArrayList;
import java.util.List;

public class ZscoreAnomalyDetector implements TimeSeriesAnomalyDetector {

    ArrayList<Float> zArr;

    public ZscoreAnomalyDetector() {
        zArr = new ArrayList<>();
    }

    @Override
    public void learnNormal(TimeSeries ts) {
        ArrayList<String> featuresNames = ts.getFeatures();

        for(int i=0;i<featuresNames.size();i++){
            ArrayList<Float> ftCol = ts.getFeatureData(featuresNames.get(i));
            float maxTh=0,currAvg,currStd,currZscore;

            for(int j=1;j<ftCol.size();j++){
                float[] arr = new float[j];

                for(int k=0;k<j;k++){
                    arr[k] = ftCol.get(k);
                }
                currAvg = StatLib.avg(arr);
                currStd = (float) Math.sqrt(StatLib.var(arr));
                currZscore = zScore(ftCol.get(j),currAvg,currStd);
                maxTh = Math.max(currZscore, maxTh);
            }

            zArr.add(maxTh);
        }

    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        ArrayList<AnomalyReport> detections = new ArrayList<>();
        ArrayList<String> featuresNames = ts.getFeatures();

        for(int i=0;i<featuresNames.size();i++){
            ArrayList<Float> ftCol = ts.getFeatureData(featuresNames.get(i));
            float maxTh=0,currAvg,currStd,currZscore;

            for(int j=1;j<ftCol.size();j++){
                float[] arr = new float[j];

                for(int k=0;k<j;k++){
                    arr[k] = ftCol.get(k);
                }
                currAvg = StatLib.avg(arr);
                currStd = (float) Math.sqrt(StatLib.var(arr));
                currZscore = zScore(ftCol.get(j),currAvg,currStd);
                if(currZscore>zArr.get(i)){
                    detections.add(new AnomalyReport(featuresNames.get(i),j+1));
                }
            }
        }
        return detections;
    }

    public float zScore(float val,float avg, float stdev){
        if(stdev==0) return 0;
        return (Math.abs(val-avg) / stdev);
    }
}
