package view;

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

        assert myPlayer.controller != null;
        vm.csvPath.bindBidirectional(myPlayer.controller.timeSeriesPath);
        myPlayer.controller.slider.valueProperty().bindBidirectional(vm.timeStep);
        myJoystick.aileron.bindBidirectional(vm.getProperty("aileron"));
        myJoystick.elevator.bindBidirectional(vm.getProperty("elevators"));
        myJoystick.rudder.bind(vm.getProperty("rudder"));
        myJoystick.throttle.bind(vm.getProperty("throttle"));
        myClocks.headingDeg.bind(vm.getProperty("heading"));
        myClocks.pitch.bind(vm.getProperty("pitch"));
        myClocks.roll.bind(vm.getProperty("roll"));
        myClocks.altimeter.bind(vm.getProperty("altitude"));

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
                case "FileNotFound": {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("File not found");
                    ApplicationStatus.pausePlayFromStart();
                    break;
                }
                case "IllegalValues": {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("Data is missing or invalid");
                    ApplicationStatus.pausePlayFromStart();
                    break;
                }
                case "XMLFormatDamaged": {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("XML Format is damaged");
                    ApplicationStatus.pausePlayFromStart();
                    break;
                }
                case "LoadedSuccessfully":{
                    ApplicationStatus.setAppColor(Color.GREEN);
                    ApplicationStatus.setAppStatusValue("Properties resource has been loaded successfully");
                    ApplicationStatus.pausePlayFromStart();
                    break;
                }
                case "LoadedCSVSuccessfully":{
                    ApplicationStatus.setAppColor(Color.GREEN);
                    ApplicationStatus.setAppStatusValue("CSV-File has been loaded successfully");
                    ApplicationStatus.pausePlayFromStart();
                    myDisplay.propList.setAll(vm.getTimeSeries().getFeatures());
                    myPlayer.controller.slider.setMin(0);
                    myPlayer.controller.slider.setMax(vm.getTimeSeries().getRowSize()-1);
                    myPlayer.controller.slider.setBlockIncrement(1);
                    myPlayer.controller.slider.setMajorTickUnit(1);
                    myPlayer.controller.slider.setMinorTickCount(0);
                    myPlayer.controller.slider.setSnapToTicks(true);
                    myPlayer.controller.slider.valueProperty().addListener(e-> System.out.println(this.myPlayer.controller.slider.valueProperty().get()));
                    break;
                }
                case "missingProperties":{
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("CSV-File is missing properties");
                    ApplicationStatus.pausePlayFromStart();
                    break;
                }
                case "incorrectFormat": {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("Incorrect CSV-File format");
                    ApplicationStatus.pausePlayFromStart();
                    break;
                }
            }

        }
    }
}
