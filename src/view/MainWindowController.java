package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import viewmodel.ViewModel;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindowController implements Observer {

    ViewModel vm;
    Stage stage;
    @FXML
    Label appStatus;



    public void loadProperties(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Project Properties");
        fc.setInitialDirectory(new File("./resources"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML Files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        //CONTINUE HERE
        if(chosenFile==null){
            vm.appStat.setValue("Failed to load resource");
        }
        else{
            vm.setAppProperties(chosenFile.getAbsolutePath());
        }
        cleanStatusBox();

    }

    public void cleanStatusBox(){
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->vm.appStat.setValue(""));
            }
        },5000l);
    }

    public void setViewModel(ViewModel vm) {
        this.vm = vm;
        appStatus.textProperty().bind(vm.appStat);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
