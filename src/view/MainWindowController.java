package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import view.clocks.Clocks;
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
    @FXML
    Clocks myClocks;

    public void loadProperties(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Project Properties");
        fc.setInitialDirectory(new File("./resources"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML Files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        if(chosenFile==null){
            applicationStatus.setAppColor(Color.RED);
            applicationStatus.setAppStatusValue("Failed to load resource");
            applicationStatus.pausePlayFromStart();
        }
        else{
            vm.setAppProperties(chosenFile.getAbsolutePath());
        }

    }

    public void initialize(ViewModel vm) {
        this.vm = vm;
        appStatus.textProperty().bindBidirectional(applicationStatus.getAppStatusStringProperty());
        appStatus.textFillProperty().bindBidirectional(applicationStatus.getAppStatus().textFillProperty());
        applicationStatus.setPauseDuration(15);
        applicationStatus.setPauseOnFinished(event->{ appStatus.setText(""); });
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
                    applicationStatus.setAppColor(Color.RED);
                    applicationStatus.setAppStatusValue("File not found");
                    applicationStatus.pausePlayFromStart();
                }
                case "IllegalValues" -> {
                    applicationStatus.setAppColor(Color.RED);
                    applicationStatus.setAppStatusValue("Data is missing or invalid");
                    applicationStatus.pausePlayFromStart();
                }
                case "XMLFormatDamaged" -> {
                    applicationStatus.setAppColor(Color.RED);
                    applicationStatus.setAppStatusValue("XML Format is damaged");
                    applicationStatus.pausePlayFromStart();
                }
                case "LoadedSuccessfully" -> {
                    applicationStatus.setAppColor(Color.GREEN);
                    applicationStatus.setAppStatusValue("Resource has been loaded successfully");
                    applicationStatus.pausePlayFromStart();
                }
                case "missingProperties" -> {
                    applicationStatus.setAppColor(Color.RED);
                    applicationStatus.setAppStatusValue("CSV-File is missing properties");
                    applicationStatus.pausePlayFromStart();
                }
                case "incorrectFormat" -> {
                    applicationStatus.setAppColor(Color.RED);
                    applicationStatus.setAppStatusValue("Incorrect CSV-File format");
                    applicationStatus.pausePlayFromStart();
                }
            }

        }
    }
}
