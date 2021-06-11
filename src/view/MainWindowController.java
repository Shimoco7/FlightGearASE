package view;

import javafx.application.Platform;
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
    @FXML Player myPlayer;
    @FXML Joystick myJoystick;
    @FXML Label appStatus;
    @FXML Clocks myClocks;
    @FXML Display myDisplay;

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
        myPlayer.controller.onPlay = vm.onPlay;
        myPlayer.controller.onStop = vm.onStop;
        myPlayer.controller.onPause = vm.onPause;
        myPlayer.controller.onFastForward = vm.onFastForward;
        myPlayer.controller.onSlowForward =vm.onSlowForward;
        myPlayer.controller.onToEnd = vm.onToEnd;
        myPlayer.controller.onToStart = vm.onToStart;

        myPlayer.controller.flightTime.textProperty().bind(vm.flightTime);
        vm.csvPath.bindBidirectional(myPlayer.controller.timeSeriesPath);
        myPlayer.controller.slider.valueProperty().bindBidirectional(vm.timeStep);
        myPlayer.controller.playSpeed.textProperty().bindBidirectional(vm.playSpeed);
        myJoystick.aileron.bindBidirectional(vm.getProperty("aileron"));
        myJoystick.elevator.bindBidirectional(vm.getProperty("elevators"));
        myJoystick.rudder.bind(vm.getProperty("rudder"));
        myJoystick.throttle.bind(vm.getProperty("throttle"));
        myClocks.headingDeg.bind(vm.getProperty("heading"));
        myClocks.pitch.bind(vm.getProperty("pitch"));
        myClocks.roll.bind(vm.getProperty("roll"));
        myClocks.altimeter.bind(vm.getProperty("altitude"));
        myClocks.yaw.bind(vm.getProperty("yaw"));
        myClocks.airspeed.bind(vm.getProperty("airspeed"));


        registerListeners();



    }

    private void registerListeners(){
        myDisplay.controller.list.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->{
            ObservableList<Float> listItem= vm.getListItem(nv.toString());
            Platform.runLater(()->myDisplay.controller.display(listItem));
        });

        myPlayer.controller.slider.setOnMouseReleased(e->{
            if(myDisplay.controller.list.getSelectionModel().getSelectedItem()!=null){
                ObservableList<Float> listItem= vm.getListItem(myDisplay.controller.list.getSelectionModel().getSelectedItem().toString());
                Platform.runLater(()->myDisplay.controller.display(listItem));
            }
        });

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
                    assert myDisplay.controller != null;
                    myDisplay.controller.list.getItems().setAll(vm.getTimeSeries().getFeatures());
                    setButtonsEnabled();

                    assert myPlayer.controller != null;
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
                    setButtonsDisabled();
                    break;
                }
                case "incorrectFormat": {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("Incorrect CSV-File format");
                    ApplicationStatus.pausePlayFromStart();
                    setButtonsDisabled();
                    break;
                }
            }

        }
    }

    private void setButtonsDisabled(){
        assert myPlayer.controller != null;
        myPlayer.controller.slider.setDisable(true);
        myPlayer.controller.play.setDisable(true);
        myPlayer.controller.stop.setDisable(true);
        myPlayer.controller.pause.setDisable(true);
        myPlayer.controller.fastForward.setDisable(true);
        myPlayer.controller.slowForward.setDisable(true);
        myPlayer.controller.toEnd.setDisable(true);
        myPlayer.controller.toStart.setDisable(true);
        myPlayer.controller.playSpeed.clear();
    }

    private void setButtonsEnabled(){
        assert myPlayer.controller != null;
        myPlayer.controller.slider.setDisable(false);
        myPlayer.controller.play.setDisable(false);
        myPlayer.controller.stop.setDisable(false);
        myPlayer.controller.pause.setDisable(false);
        myPlayer.controller.fastForward.setDisable(false);
        myPlayer.controller.slowForward.setDisable(false);
        myPlayer.controller.toEnd.setDisable(false);
        myPlayer.controller.toStart.setDisable(false);
        myPlayer.controller.playSpeed.setText("1.0");
    }

}
