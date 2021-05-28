package view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import view.clocks.Clocks;
import view.display.Display;
import view.joystick.Joystick;
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
    Joystick myJoystick;
    @FXML
    Label appStatus;
    @FXML
    Clocks myClocks;
    @FXML
    Display myDisplay;

    public void loadProperties(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Project Properties");
        fc.setInitialDirectory(new File("./resources"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML Files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        if(chosenFile==null){
            ApplicationStatus.setAppColor(Color.RED);
            ApplicationStatus.setAppStatusValue("Failed to load resource");
            ApplicationStatus.pausePlayFromStart();
        }
        else{
            vm.setAppProperties(chosenFile.getAbsolutePath());
        }

    }

    public void initialize(ViewModel vm) {
        this.vm = vm;
        appStatus.textProperty().bindBidirectional(ApplicationStatus.getAppStatusStringProperty());
        appStatus.textFillProperty().bindBidirectional(ApplicationStatus.getAppStatus().textFillProperty());
        ApplicationStatus.setPauseDuration(15);
        ApplicationStatus.setPauseOnFinished(event->{ appStatus.setText(""); });
        vm.csvPath.bindBidirectional(myPlayer.timeSeriesPath);

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
            switch (arg.toString()) {
                case "FileNotFound" -> {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("File not found");
                    ApplicationStatus.pausePlayFromStart();
                }
                case "IllegalValues" -> {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("Data is missing or invalid");
                    ApplicationStatus.pausePlayFromStart();
                }
                case "XMLFormatDamaged" -> {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("XML Format is damaged");
                    ApplicationStatus.pausePlayFromStart();
                }
                case "LoadedSuccessfully" -> {
                    ApplicationStatus.setAppColor(Color.GREEN);
                    ApplicationStatus.setAppStatusValue("Properties resource has been loaded successfully");
                    ApplicationStatus.pausePlayFromStart();
                }
                case "LoadedCSVSuccessfully" ->{
                    ApplicationStatus.setAppColor(Color.GREEN);
                    ApplicationStatus.setAppStatusValue("CSV-File has been loaded successfully");
                    ApplicationStatus.pausePlayFromStart();
                    myDisplay.propList.setAll(vm.getTimeSeries().getFeatures());
                }
                case "missingProperties" -> {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("CSV-File is missing properties");
                    ApplicationStatus.pausePlayFromStart();
                }
                case "incorrectFormat" -> {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("Incorrect CSV-File format");
                    ApplicationStatus.pausePlayFromStart();
                }
            }

        }
    }
}
