package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import model.FGModel;
import model.Model;
import other.Properties;
import ptm1.TimeSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    Model m;
    TimeSeries timeSeries;
    Properties appProp;
    private HashMap<String, DoubleProperty> displayVariables;
    public IntegerProperty timeStep;
    public StringProperty csvPath,playSpeed;
    public final Runnable onPlay,onStop,onPause,onFastForward, onSlowForward,onToStart,onToEnd;


    public ViewModel(Model m) {
        this.m = m;
        displayVariables = new HashMap<>();
        csvPath= new SimpleStringProperty();
        timeStep = new SimpleIntegerProperty();
        playSpeed = new SimpleStringProperty();
        m.setTimeStep(this.timeStep);
        appProp = m.getProperties();
        initDisplayVariables();

        csvPath.addListener((o,ov,nv)->{
            String res = this.m.uploadCsv(nv);
            if (res.equals("LoadedCSVSuccessfully")) {
                timeSeries = new TimeSeries(nv);
                this.m.setTimeSeries(timeSeries);
            }
            setChanged();
            notifyObservers(res);
        });

        timeStep.addListener((o,ov,nv)->{
            ArrayList<Float> row = timeSeries.getRow((Integer) nv);
            Platform.runLater(() -> {
                displayVariables.get("aileron").set(row.get(appProp.getMap().get("aileron").getIndex()));
                displayVariables.get("elevators").set(row.get(appProp.getMap().get("elevators").getIndex()));
                displayVariables.get("rudder").set(row.get(appProp.getMap().get("rudder").getIndex()));
                displayVariables.get("throttle").set(row.get(appProp.getMap().get("throttle").getIndex()));
                displayVariables.get("altitude").set(row.get(appProp.getMap().get("altitude").getIndex()));
                displayVariables.get("heading").set(-(row.get(appProp.getMap().get("heading").getIndex())));
                displayVariables.get("pitch").set(row.get(appProp.getMap().get("pitch").getIndex()));
                displayVariables.get("roll").set(row.get(appProp.getMap().get("roll").getIndex()));
                displayVariables.get("yaw").set(row.get(appProp.getMap().get("yaw").getIndex()));
                displayVariables.get("airspeed").set(row.get(appProp.getMap().get("airspeed").getIndex()));
                displayVariables.get("longitude").set(row.get(appProp.getMap().get("longitude").getIndex()));
                displayVariables.get("latitude").set(row.get(appProp.getMap().get("latitude").getIndex()));
            });
        });

        onPlay = ()->m.play();
        onStop = ()->m.stop();
        onPause = ()->m.pause();
        onFastForward = ()->m.fastForward();
        onSlowForward = ()->m.slowForward();
        onToStart = ()->m.skipToStart();
        onToEnd = ()->m.skipToEnd();

    }

    public DoubleProperty getProperty(String name){
        if(displayVariables.containsKey(name))
            return displayVariables.get(name);
        else return null;
    }

    private void initDisplayVariables(){
        if(displayVariables.keySet().size()!=0){
            displayVariables.clear();
        }
        for(String feature : appProp.getMap().keySet()){
            displayVariables.put(feature, new SimpleDoubleProperty());
        }
    }
    public void setAppProperties(String path){
        m.setProperties(path);
    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(FGModel.class)){

            switch (arg.toString()) {
                case "FileNotFound":{
                    setChanged();
                    notifyObservers("FileNotFound");
                    break;
                }
                case "IllegalValues": {
                    setChanged();
                    notifyObservers("IllegalValues");
                    break;
                }
                case "XMLFormatDamaged": {
                    setChanged();
                    notifyObservers("XMLFormatDamaged");
                    break;
                }
                case "LoadedSuccessfully": {
                    setChanged();
                    notifyObservers("LoadedSuccessfully");
                    break;
                }
            }

            switch((FGModel.playSpeed)arg){
                case NORMAL:{
                    playSpeed.set("1.0");
                    break;
                }

                case FASTER:{
                    playSpeed.set("2.0");
                    break;
                }

                case FASTEST:{
                    playSpeed.set("4.0");
                    break;
                }
            }

            if(arg.getClass().equals(Properties.class)){
                appProp = (Properties) arg;
                initDisplayVariables();
            }
        }
    }
}
