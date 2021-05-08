package Model;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

    ArrayList<CorrelatedFeatures> corFeatures;
    float corlThreshold;

    public SimpleAnomalyDetector() {
        corFeatures = new ArrayList<CorrelatedFeatures>();
        corlThreshold = (float)0.9;
    }

    @Override
    //learning offline normal features and data
    public void learnNormal(TimeSeries ts) {
        ArrayList<String> ft=ts.getFeatures();
        int len=ts.getRowSize();

        float[][] vals =new float[ft.size()][len];
        for(int i=0;i<ft.size();i++){
            for(int j=0;j<ts.getRowSize();j++){
                vals[i][j]=ts.getFeatureData(ft.get(i)).get(j);
            }
        }

        for(int i=0;i<ft.size();i++){
            for(int j=i+1;j<ft.size();j++){
                float p=StatLib.pearson(vals[i],vals[j]);
                if(Math.abs(p)>corlThreshold){ 										//Strong Correlation between the features

                    Point ps[]=toPoints(ts.getFeatureData(ft.get(i)),ts.getFeatureData(ft.get(j)));
                    Line lin_reg=StatLib.linear_reg(ps); 					//Line Regression of the Correlated-Features
                    float threshold=findThreshold(ps,lin_reg)*1.1f; 		// 10% increase to cover normal points around the normal area

                    CorrelatedFeatures c=new CorrelatedFeatures(ft.get(i), ft.get(j), p, lin_reg, threshold);

                    corFeatures.add(c);
                }
            }
        }
    }


    private Point[] toPoints(ArrayList<Float> x, ArrayList<Float> y) {
        Point[] ps=new Point[x.size()];
        for(int i=0;i<ps.length;i++)
            ps[i]=new Point(x.get(i),y.get(i));
        return ps;
    }


    //Maximum deviation of each point of the Correlation from the Line-regression
    private float findThreshold(Point ps[],Line rl){
        float max=0;
        for(int i=0;i<ps.length;i++){
            float d=Math.abs(ps[i].y - rl.f(ps[i].x));
            if(d>max)
                max=d;
        }
        return max;
    }

    @Override
    //Online detection of Anomalies during Time-Series input
    public List<AnomalyReport> detect(TimeSeries ts) {
        ArrayList<AnomalyReport> ar=new ArrayList<>();

        for(CorrelatedFeatures c : corFeatures) {
            ArrayList<Float> x=ts.getFeatureData(c.feature1);
            ArrayList<Float> y=ts.getFeatureData(c.feature2);
            for(int i=0;i<x.size();i++){
                // For each point of the correlated features, if the point deviation
                // from the line-regression exceeds the threshold it indicates an anomaly
                if(Math.abs(y.get(i) - c.lin_reg.f(x.get(i)))>c.threshold){
                    String d=c.feature1 + "-" + c.feature2;
                    //Time-steps in any given time series start from 1, thus k will be send to a new Anomaly-Report as k+1
                    ar.add(new AnomalyReport(d,(i+1)));
                }
            }
        }
        return ar;
    }

    public List<CorrelatedFeatures> getNormalModel(){
        return corFeatures;
    }

    public float getCorlThreshold() {
        return corlThreshold;
    }

    public void setCorlThreshold(float corlThreshold) {
        this.corlThreshold = corlThreshold;
    }




}
