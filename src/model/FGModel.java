package model;

import javafx.beans.property.IntegerProperty;
import other.Properties;
import ptm1.TimeSeries;
import ptm1.TimeSeriesAnomalyDetector;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

public class FGModel extends Observable implements Model{

    Properties appProperties;
    IntegerProperty timeStep;
    TimeSeries ts;
    FGPlayer fgp;
    Timer t;
    int hertzRate;
    playSpeed ps;

    public FGModel() {
        setProperties("./resources/properties.xml");
        fgp = new FGPlayer(appProperties);
        hertzRate= appProperties.getHertzRate();
        ps = playSpeed.NORMAL;
    }

    @Override
    public void start() {

    }

    @Override
    public void setTimeSeries(TimeSeries ts) {
        this.ts = ts;
    }

    @Override
    public void setProperties(String path) {
        XMLDecoder d = null;
        try {
            d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream(path)));
        } catch (FileNotFoundException e) {
           e.printStackTrace();
            setChanged();
            notifyObservers("FileNotFound");
        }
        try {
            Properties tempProperties =(Properties) d.readObject();
            if(!tempProperties.isValidProperties()){
                setChanged();
                notifyObservers("IllegalValues");
            }
            else{
                appProperties = tempProperties;
                setChanged();
                notifyObservers("LoadedSuccessfully");
                notifyObservers(appProperties);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            setChanged();
            notifyObservers("XMLFormatDamaged");
        }
        d.close();

        XMLEncoder e = null;
        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("./resources/properties.xml")));
            e.writeObject(appProperties);

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        e.close();
    }

    @Override
    public void setTimeStep(IntegerProperty timeStep) {
        this.timeStep = timeStep;
    }

    @Override
    public <V> Properties getProperties() {
        return appProperties;
    }

    @Override
    public void setAnomalyDetector(TimeSeriesAnomalyDetector ad) {

    }

    @Override
    public void play() {
        if(t==null){
            t= new Timer();
            setPlaySpeed();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(timeStep.get()<ts.getRowSize()-1) {
                        timeStep.set(timeStep.get() + 1);
                    }
                    else if(timeStep.get()==ts.getRowSize()-1){
                        t.cancel();
                        t=null;
                    }
                }
            }, 0, hertzRate * 10);
        }
    }

    @Override
    public void skipToStart() {
       stop();
       play();
    }

    @Override
    public void skipToEnd() {
       if(t!=null){
           t.cancel();
           t=null;
       }
       timeStep.set(ts.getRowSize()-1);
    }

    @Override
    public void fastForward() {

            if(ps==playSpeed.SLOWEST){
                ps=playSpeed.SLOWER;
                setChanged();
                notifyObservers(playSpeed.SLOWER);
            }
            else if(ps==playSpeed.SLOWER){
                ps=playSpeed.NORMAL;
                setChanged();
                notifyObservers(playSpeed.NORMAL);
            }
            else if(ps==playSpeed.NORMAL){
                ps=playSpeed.FASTER;
                setChanged();
                notifyObservers(playSpeed.FASTER);
            }
            else if(ps==playSpeed.FASTER){
                ps=playSpeed.FASTEST;
                setChanged();
                notifyObservers(playSpeed.FASTEST);
            }

        if(t!=null) {
            t.cancel();
            t = null;
            play();
        }
    }


    @Override
    public void slowForward() {
            if(ps==playSpeed.SLOWER){
                ps=playSpeed.SLOWEST;
                setChanged();
                notifyObservers(playSpeed.SLOWEST);
            }
            else if(ps==playSpeed.NORMAL){
                ps=playSpeed.SLOWER;
                setChanged();
                notifyObservers(playSpeed.SLOWER);
            }
            else if(ps==playSpeed.FASTEST){
                ps=playSpeed.FASTER;
                setChanged();
                notifyObservers(playSpeed.FASTER);
            }
            else if(ps==playSpeed.FASTER){
                ps=playSpeed.NORMAL;
                setChanged();
                notifyObservers(playSpeed.NORMAL);
            }

        if(t!=null) {
            t.cancel();
            t = null;
            play();
        }
    }


    @Override
    public void pause() {
        if(t!=null){
            t.cancel();
            t=null;
        }
    }

    @Override
    public void stop() {
        if(t!=null){
            t.cancel();
            t=null;
        }
        timeStep.set(0);
        ps=playSpeed.NORMAL;
        setChanged();
        notifyObservers(playSpeed.NORMAL);
    }

    @Override
    public String uploadCsv(String nv) {
        HashSet<String> set = new HashSet<>();
        LinkedHashMap<String, Properties.FeatureProperties> map = appProperties.getMap();

//        for(Properties.FeatureProperties fp : map.values()){
//            set.add(fp.getIndex());
//        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(nv)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = scanner.nextLine();
        String[] features= line.split(",");
//        for(String feature: features){
//            if(set.contains(feature)){
//                set.remove(feature);
//            }
//        }
//
//        if(set.size()!=0)
//            return "missingProperties";

        while(scanner.hasNext()){
            features = scanner.next().split(",");
            for(String f : features){
                try{
                    Double.parseDouble(f);
                }
                catch (NumberFormatException e){
                    return "incorrectFormat";
                }
            }
        }
        scanner.close();

        return "LoadedCSVSuccessfully";
    }

    public Properties getAppProperties() {
        return appProperties;
    }

    private void setPlaySpeed(){
        switch(ps){
            case SLOWEST:{
                hertzRate = appProperties.getHertzRate()*4;
                break;
            }
            case SLOWER:{
                hertzRate=appProperties.getHertzRate()*2;
                break;
            }
            case NORMAL: {
                hertzRate = appProperties.getHertzRate();
                break;
            }
            case FASTER:{
                if(hertzRate>2)
                    hertzRate = appProperties.getHertzRate()/2;
                break;
            }
            case FASTEST:{
                if(hertzRate>4)
                    hertzRate = appProperties.getHertzRate()/4;
                break;
            }
        }
    }

    public enum playSpeed{
        SLOWEST,
        SLOWER,
        NORMAL,
        FASTER,
        FASTEST
    }
}
