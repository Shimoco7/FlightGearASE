package viewmodel;

import javafx.beans.property.*;
import model.FGModel;
import model.Model;
import other.Properties;
import ptm1.TimeSeries;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    Model m;
    TimeSeries timeSeries;
    Properties appProp;
    private HashMap<String, DoubleProperty> displayVariables;
    public StringProperty csvPath;


    public ViewModel(Model m) {
        this.m = m;
        displayVariables = new HashMap<>();
        csvPath= new SimpleStringProperty();
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

            if(arg.getClass().equals(Properties.class)){
                appProp = (Properties) arg;
                initDisplayVariables();
            }
        }
    }
}
