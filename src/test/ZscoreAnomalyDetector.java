package test;

import java.util.ArrayList;
import java.util.List;

public class ZscoreAnomalyDetector implements TimeSeriesAnomalyDetector {

    ArrayList<zScoreParameters> zArr;

    public ZscoreAnomalyDetector() {
        zArr = new ArrayList<>();
    }

    @Override
    public void learnNormal(TimeSeries ts) {
        ArrayList<String> featuresNames = ts.getFeatures();

        for(int i=0;i<featuresNames.size();i++){
            ArrayList<Float> ftCol = ts.getFeatureData(featuresNames.get(i));
            float maxTh=0;
            float avgX, standardDev;

            float[] arr = new float[ftCol.size()];
            int index = 0;
            for ( Float value: ftCol)
                arr[index++] = value;

            avgX = StatLib.avg(arr);
            standardDev = (float) Math.sqrt(StatLib.var(arr));

            for(int j=0;j<ts.getRowSize();j++){
                float temp = zScore(arr[j],avgX,standardDev);
                maxTh = (temp > maxTh) ? temp : maxTh;
            }
            zArr.add(new zScoreParameters(avgX,standardDev,maxTh));
        }

    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        ArrayList<AnomalyReport> detections = new ArrayList<>();
        ArrayList<String> featuresNames = ts.getFeatures();
        for(int i=0;i<ts.getFeatures().size();i++){
            ArrayList<Float> featDataX = ts.getFeatureData(featuresNames.get(i));
            for(int j=0;j<ts.getRowSize();j++){
                float currZscore = zScore(featDataX.get(j),zArr.get(i).avg,zArr.get(i).standardDeviation);
                if(currZscore>=zArr.get(i).maxTh){
                   detections.add(new AnomalyReport(featuresNames.get(i),j+1));
                }
            }
        }

        return detections;
    }

    public float zScore(float val,float avg, float stdev){
        return (Math.abs(val-avg) / stdev);
    }

    private class zScoreParameters{
        float avg,standardDeviation,maxTh;

        public zScoreParameters(float avg, float standardDeviation, float maxTh) {
            this.avg = avg;
            this.standardDeviation = standardDeviation;
            this.maxTh = maxTh;
        }
    }
}
