package view;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.util.Duration;
import view.player.Player;
import viewmodel.ViewModel;

import java.io.File;
import java.util.Observable;
import java.util.Observer;


public class MainWindowController implements Observer {

    ViewModel vm;
    Stage stage;
    @FXML
    Player myPlayer;
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

        if(chosenFile==null){
            appStatus.setTextFill(Color.RED);
            appStatus.setText("Failed to load resource");;
        }
        else{
            vm.setAppProperties(chosenFile.getAbsolutePath());
        }
        applicationStatus.pausePlayFromStart();

    }

    public void initialize(ViewModel vm) {
        this.vm = vm;
        appStatus.textProperty().bindBidirectional(applicationStatus.getAppStatusProperty());
        applicationStatus.setPauseDuration(15);
        applicationStatus.setPauseOnFinished(event->{ appStatus.setText(""); });
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(ViewModel.class)){
            if(arg.toString().equals("FileNotFound")){
                appStatus.setTextFill(Color.RED);
                appStatus.setText("File not found");
            }
            else if(arg.toString().equals("IllegalValues")){
                appStatus.setTextFill(Color.RED);
                appStatus.setText("Data is missing or invalid");
            }
            else if(arg.toString().equals("XMLFormatDamaged")){
                appStatus.setTextFill(Color.RED);
                appStatus.setText("XML Format is damaged");
            }
            else if(arg.toString().equals("LoadedSuccessfully")){
                appStatus.setTextFill(Color.GREEN);
                appStatus.setText("Resource has been loaded successfully");
            }
        }
    }
}
