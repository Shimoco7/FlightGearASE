package viewmodel;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.FGModel;
import model.Model;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    Model m;
    public FloatProperty aileron,elevators,rudder, throttle,
        airspeed,heading,roll,pitch,yaw,altitude,longitude,latitude;

    public ViewModel(Model m) {
        this.m = m;

        aileron = new SimpleFloatProperty();
        elevators = new SimpleFloatProperty();
        rudder = new SimpleFloatProperty();
        throttle = new SimpleFloatProperty();
        airspeed = new SimpleFloatProperty();
        heading = new SimpleFloatProperty();
        roll = new SimpleFloatProperty();
        pitch = new SimpleFloatProperty();
        yaw = new SimpleFloatProperty();
        altitude = new SimpleFloatProperty();
        longitude = new SimpleFloatProperty();
        latitude = new SimpleFloatProperty();

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
