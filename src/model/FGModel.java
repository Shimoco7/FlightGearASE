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

    public FGModel() {
        setProperties("./resources/properties.xml");
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
        appProperties = (Properties) d.readObject();
        d.close();
        setChanged();
        notifyObservers("LoadedSuccessfully");

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
