package model;

import other.Properties;
import ptm1.TimeSeries;
import ptm1.TimeSeriesAnomalyDetector;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Observable;

public class FGModel extends Observable implements Model{

    Properties appProperties;
    FGPlayer fgp;

    public FGModel() {
        setProperties("./resources/properties.xml");
        fgp = new FGPlayer(appProperties);
    }

    @Override
    public void start() {

    }

    @Override
    public void setTimeSeries(TimeSeries ts) {

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
    public void setAnomalyDetector(TimeSeriesAnomalyDetector ad) {

    }

    @Override
    public void play(int start, int rate) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }
}
