package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Commands {

    // Default IO interface
    public interface DefaultIO{
        public String readText();
        public void write(String text);
        public float readVal();
        public void write(float val);


    }

    // the default IO to be used in all commands
    DefaultIO dio;
    public Commands(DefaultIO dio) {
        this.dio=dio;
    }

    public SharedState getSharedState() {
        return sharedState;
    }

    public void setSharedState(SharedState sharedState) {
        this.sharedState = sharedState;
    }

    public static class StandardIO implements DefaultIO{

        Scanner in;
        public StandardIO() {
            in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        }


        @Override
        public String readText() {
            return in.nextLine();
        }

        @Override
        public void write(String text) {
            System.out.print(text);
        }

        @Override
        public float readVal() {
            return in.nextFloat();
        }

        @Override
        public void write(float val) {
            System.out.print(val);
        }

    }



    // the shared state of all commands
    private class SharedState{
        TimeSeries tsTrain;
        TimeSeries tsTest;
        int numOfRows;
        float threshold;
        List<AnomalyReport> ar;

        public SharedState() {
            this.threshold = (float)0.9;
            this.numOfRows = -1;
        }

    }

    private  SharedState sharedState=new SharedState();


    // Command abstract class
    public abstract class Command{
        protected String description;

        public Command(String description) {
            this.description=description;
        }

        public abstract void execute();
    }


    //Commands:
    //Command 1 - upload command
    public class UploadCommand extends Command{

        public UploadCommand() {
            super("upload a time series csv file");
        }
        @Override
        public void execute() {
            dio.write("Please upload your local train CSV file.\n");

            try {
                PrintWriter trainFile = new PrintWriter(new FileWriter("anomalyTrain.csv"));
                String line;
                while(true) {
                    line = dio.readText();
                    if(line.equals("done"))
                        break;
                    trainFile.write(line+"\n");
                }
                trainFile.close();
                sharedState.tsTrain = new TimeSeries("anomalyTrain.csv");
                dio.write("Upload complete.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            dio.write("Please upload your local test CSV file.\n");
            try {
                PrintWriter testFile = new PrintWriter(new FileWriter("anomalyTest.csv"));
                String line;
                while(true) {
                    line = dio.readText();
                    if(line.equals("done"))
                        break;
                    testFile.write(line+"\n");
                    sharedState.numOfRows++;
                }
                testFile.close();
                sharedState.tsTest = new TimeSeries("anomalyTest.csv");
                dio.write("Upload complete.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public String toString() {
            return this.description;
        }

    }

    //Command 2 - change algorithm settings command
    public class AlgorithmSettingsCommand extends Command{

        public AlgorithmSettingsCommand() {
            super("algorithm settings");
        }
        @Override
        public void execute() {
            dio.write("The current correlation threshold is "+sharedState.threshold+"\n");
            dio.write("Type a new threshold\n");
            float tsh = sharedState.threshold;
            while(true) {
                String c=dio.readText();
                tsh = Float.parseFloat(c);
                if(tsh<0||tsh>1)
                    dio.write("please choose a value between 0 and 1.\n");
                else
                    break;
            }
            sharedState.threshold=tsh;
        }

        @Override
        public String toString() {
            return this.description;
        }

    }

    //Command 3 - run algorithm command
    public class AnomalyDetectionCommand extends Command{

        public AnomalyDetectionCommand() {
            super("detect anomalies");
        }
        @Override
        public void execute() {
            if(sharedState.tsTrain==null||sharedState.tsTest==null)
                return;
            SimpleAnomalyDetector sad = new SimpleAnomalyDetector();
            sad.setCorlThreshold(sharedState.threshold);
            sad.learnNormal(sharedState.tsTrain);
            List<AnomalyReport> AnoRepo = sad.detect(sharedState.tsTest);
            sharedState.ar = AnoRepo;

            dio.write("anomaly detection complete.\n");
        }

        @Override
        public String toString() {
            return this.description;
        }

    }

    //Command 4 - display algorithm results command
    public class DisplayResultsCommand extends Command{

        public DisplayResultsCommand() {
            super("display results");
        }
        @Override
        public void execute() {
            if(sharedState.ar==null)
                return;
            for (AnomalyReport a : sharedState.ar ) {
                dio.write(a.timeStep+"\t"+a.description+"\n");
            }
            dio.write("Done.\n");
        }

        @Override
        public String toString() {
            return this.description;
        }

    }

    //Command 5 - upload anomalies report file and compare its anomalies
    //			  to the algorithm results
    public class UploadAnomalisAndAnalyzeCommand extends Command{

        public UploadAnomalisAndAnalyzeCommand() {
            super("upload anomalies and analyze results");
        }
        @Override
        public void execute() {
            if(sharedState.ar==null)
                return;
            dio.write("Please upload your local anomalies file.\n");
            List<contAnoReport> crList = new ArrayList<contAnoReport>();
            int j=0;
            //combining the same consecutive anomalies into a single object(contAnoReport)
            for(int i=0;i<sharedState.ar.size();i++) {
                while(j<sharedState.ar.size()-1&&
                        sharedState.ar.get(i).description.equals(sharedState.ar.get(j+1).description)) {
                    j++;
                }
                contAnoReport cr = new contAnoReport(sharedState.ar.get(i).timeStep, sharedState.ar.get(j).timeStep);
                crList.add(cr);
                i=j;
            }
            //p will represent the number of actual anomalies in the anomaly-detector file
            //that has been uploaded by the user
            int p=0;
            double tp=0, fp=0;
            //n will represent the number of rows where anomaly hasn't been detected
            int n = sharedState.numOfRows;

            String line;
            while(true) {
                line = dio.readText();
                if(line.equals("done")) {
                    dio.write("Upload complete.\n");
                    break;
                }
                p++;
                String start = line.split(",")[0];
                String end = line.split(",")[1];
                for(contAnoReport c : crList) {
                    //checks whether the algorithms anomaly reports(contAnoReport) have intersection
                    //with the anomaly report file
                    if(isContained(Integer.parseInt(start),Integer.parseInt(end),c.tsStart,c.tsEnd)) {
                        c.tp = true;
                        n= n-(Integer.parseInt(end)-Integer.parseInt(start)+1);
                        break;
                    }
                }
            }

            for(contAnoReport c : crList) {
                if(c.tp)
                    tp++;
                else
                    fp++;
            }
            //prints maximum of 3 fraction-digits
            dio.write("True Positive Rate: " + (float)(Math.floor((tp / p)*1000)/1000)+"\n");
            dio.write("False Positive Rate: " + (float)(Math.floor((fp / n)*1000)/1000)+"\n");
        }

        private class contAnoReport{
            long tsStart; //time-step beginning
            long tsEnd;	  //time-step end
            boolean tp;   //true-positive

            public contAnoReport(long timeStep,long timeStep2) {
                this.tsStart = timeStep;
                this.tsEnd = timeStep2;
            }
        }
        @Override
        public String toString() {
            return this.description;
        }

    }

    //checks intersection between two sections
    public static boolean isContained(long ts1Start,long ts1End,long ts2Start,long ts2End) {
        if(ts2Start<=ts1End&&ts2Start>=ts1Start||
                ts2End<=ts1End&&ts2End>=ts1Start||
                ts1Start<=ts2End&&ts1Start>=ts2Start||
                ts1End<=ts2End&&ts1End>=ts2Start)
            return true;
        else
            return false;

    }

    public class exitCommand extends Command{

        public exitCommand() {
            super("exit");
        }
        @Override
        public void execute() {
        }

        @Override
        public String toString() {
            return this.description;
        }

    }
}
