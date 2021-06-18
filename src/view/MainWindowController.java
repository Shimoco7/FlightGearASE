package view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import ptm1.Painter;
import view.clocks.Clocks;
import view.display.Display;
import view.joystick.Joystick;
import view.player.Player;
import viewmodel.ViewModel;

import java.io.File;
import java.util.Observable;
import java.util.Observer;


public class MainWindowController implements Observer {

    private ViewModel vm;
    private Stage stage;
    private Painter painter;
    private @FXML Player myPlayer;
    private @FXML Joystick myJoystick;
    private @FXML Label appStatus;
    private @FXML Clocks myClocks;
    private @FXML Display myDisplay;
    private @FXML MenuItem loadAlgorithm;

    public void loadProperties(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Project Properties");
        fc.setInitialDirectory(new File("./resources"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML Files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        if(chosenFile!=null){
            vm.setAppProperties(chosenFile.getAbsolutePath());
        }
    }
    
    public void loadAlgorithm(){
		FileChooser fc = new FileChooser();
        fc.setTitle("Load Detection Algorithm");
        fc.setInitialDirectory(new File("./algorithms"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
        		"Class Files (*.class)", "*.class");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        if(chosenFile!=null){
            vm.setAlgorithm(chosenFile.getAbsolutePath());
        }
    }

    public void initialize(ViewModel vm) {
        this.vm = vm;
        loadAlgorithm.setDisable(true);
        appStatus.textProperty().bindBidirectional(ApplicationStatus.getAppStatusProp());
        appStatus.textFillProperty().bindBidirectional(ApplicationStatus.getAppStatus().textFillProperty());
        appStatus.styleProperty().bindBidirectional(ApplicationStatus.getAppStyleProp());
        ApplicationStatus.setPauseDuration(15);
        ApplicationStatus.setPauseOnFinished(event-> {
            appStatus.setText("");
            appStatus.setStyle("");
        });
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
        //Time-Step Listener
        vm.timeStep.addListener((o,ov,nv)->{
            int timeStep= vm.timeStep.get();
            if(myDisplay.controller.list.getSelectionModel().getSelectedItem()!=null) {
                String selectedFeature = myDisplay.controller.list.getSelectionModel().getSelectedItem().toString();
                ObservableList<Float> leftListItem,rightListItem;
                if(nv.intValue()<=ov.intValue()){
                    leftListItem= vm.getListItem(selectedFeature,0,timeStep);
                    rightListItem = vm.getCorrelatedListItem(selectedFeature,0,timeStep);
                    Platform.runLater(()->myDisplay.controller.display(leftListItem,rightListItem));
                }
                else {
                    leftListItem= vm.getListItem(selectedFeature, ov.intValue(), nv.intValue());
                    rightListItem = vm.getCorrelatedListItem(selectedFeature,ov.intValue(), nv.intValue());
                    Platform.runLater(() -> myDisplay.controller.updateDisplay(leftListItem,rightListItem, ov.intValue()));
                }
                if(painter!=null){
                    Platform.runLater(()->painter.paint(myDisplay.controller.mainGraph, ov.intValue(), nv.intValue(), selectedFeature));
                }
            }
        });

        //ListItem Listener
        myDisplay.controller.list.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->{
            if(nv!=null) {
                ObservableList<Float> leftListItem = vm.getListItem(nv.toString(), 0, vm.timeStep.get());
                ObservableList<Float> rightListItem = vm.getCorrelatedListItem(nv.toString(), 0, vm.timeStep.get());
                Platform.runLater(() -> myDisplay.controller.display(leftListItem,rightListItem));
                Platform.runLater(()->painter.paint(myDisplay.controller.mainGraph,0,vm.timeStep.get(),nv.toString()));
            }
        });

        stage.setOnCloseRequest(e->vm.close());

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
                    setButtonsDisabled();
                    break;
                }
                case "XMLFormatDamaged": {
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("XML Format is damaged");
                    ApplicationStatus.pausePlayFromStart();
                    setButtonsDisabled();
                    vm.csvPath.set("");
                    break;
                }

                case "LoadedSuccessfully":{
                    ApplicationStatus.setAppColor(Color.GREEN);
                    ApplicationStatus.setAppStatusValue("Properties resource has been loaded successfully");
                    ApplicationStatus.pausePlayFromStart();
                    setButtonsDisabled();
                    vm.csvPath.set("");
                    break;
                }
                case "LoadedCSVSuccessfully":{
                    ApplicationStatus.setAppColor(Color.GREEN);
                    ApplicationStatus.setAppStatusValue("CSV-File has been loaded successfully");
                    ApplicationStatus.pausePlayFromStart();
                    assert myDisplay.controller != null;
                    myDisplay.controller.list.getItems().setAll(vm.getTimeSeries().getFeatures());
                    vm.onStop.run();
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
                case "dataOutOfRange":{
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("One or more data values is out of feature's legal range");
                    ApplicationStatus.pausePlayFromStart();
                    setButtonsDisabled();
                    break;
                }
                case "LoadedClassSuccessfully":{
                    ApplicationStatus.setAppColor(Color.GREEN);
                    ApplicationStatus.setAppStatusValue("Class-File has been loaded successfully");
                    painter = vm.getPainter();
                    break;
                }
                case "FailedToLoadClass":{
                    ApplicationStatus.setAppColor(Color.RED);
                    ApplicationStatus.setAppStatusValue("Failed to load the class file");
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
        myPlayer.controller.slider.valueProperty().set(0);
        vm.flightTime.set("00:00:00");
        assert myDisplay.controller != null;
        myDisplay.controller.list.getItems().clear();
        loadAlgorithm.setDisable(true);
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
        loadAlgorithm.setDisable(false);
    }

    public void close(ActionEvent actionEvent) {
        vm.close();
        stage.close();
    }
}
