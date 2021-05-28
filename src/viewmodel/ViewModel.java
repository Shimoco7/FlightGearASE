package viewmodel;

import javafx.beans.property.*;
import model.FGModel;
import model.Model;
import ptm1.TimeSeries;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    Model m;
    TimeSeries timeSeries;
    public DoubleProperty aileron,elevators,rudder, throttle,
        airspeed,heading,roll,pitch,yaw,altitude,longitude,latitude;
    public StringProperty csvPath;


    public ViewModel(Model m) {
        this.m = m;
        csvPath= new SimpleStringProperty();
        aileron = new SimpleDoubleProperty();
        elevators = new SimpleDoubleProperty();
        rudder = new SimpleDoubleProperty();
        throttle = new SimpleDoubleProperty();
        airspeed = new SimpleDoubleProperty();
        heading = new SimpleDoubleProperty();
        roll = new SimpleDoubleProperty();
        pitch = new SimpleDoubleProperty();
        yaw = new SimpleDoubleProperty();
        altitude = new SimpleDoubleProperty();
        longitude = new SimpleDoubleProperty();
        latitude = new SimpleDoubleProperty();

        csvPath.addListener((o,ov,nv)->{
            String res = this.m.uploadCsv(nv);
            if (res.equals("LoadedSuccessfully")) {
                timeSeries = new TimeSeries(nv);
                this.m.setTimeSeries(timeSeries);
            }
            setChanged();
            notifyObservers(res);
        });
    }

    public void setAppProperties(String path){
        m.setProperties(path);
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(FGModel.class)){

            switch (arg.toString()) {
                case "FileNotFound" -> {
                    setChanged();
                    notifyObservers("FileNotFound");
                }
                case "IllegalValues" -> {
                    setChanged();
                    notifyObservers("IllegalValues");
                }
                case "XMLFormatDamaged" -> {
                    setChanged();
                    notifyObservers("XMLFormatDamaged");
                }
                case "LoadedSuccessfully" -> {
                    setChanged();
                    notifyObservers("LoadedSuccessfully");
                }
            }
        }
    }
}
