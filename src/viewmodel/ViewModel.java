package viewmodel;

import javafx.beans.property.*;
import model.FGModel;
import model.Model;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    Model m;
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

    }

    public void setAppProperties(String path){
        m.setProperties(path);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(FGModel.class)){

            if(arg.toString().equals("FileNotFound")){
                setChanged();
                notifyObservers("FileNotFound");
            }
            else if(arg.toString().equals("IllegalValues")){
                setChanged();
                notifyObservers("IllegalValues");
            }
            else if(arg.toString().equals("XMLFormatDamaged")){
                setChanged();
                notifyObservers("XMLFormatDamaged");
            }
            else if(arg.toString().equals("LoadedSuccessfully")){
                setChanged();
                notifyObservers("LoadedSuccessfully");
            }
        }
    }
}
