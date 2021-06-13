package view.player;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.Main;
import view.ApplicationStatus;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML public TextField playSpeed;
    @FXML public Label flightTime;
    @FXML public Slider slider;
    @FXML public Button play,stop,pause,fastForward, slowForward,toEnd,toStart;
    Stage stage;
    public StringProperty timeSeriesPath;
    public Runnable onPlay,onStop,onPause,onFastForward, onSlowForward,onToStart,onToEnd;


    public PlayerController() {
        stage = Main.getGuiStage();
        timeSeriesPath = new SimpleStringProperty();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slider.setDisable(true);
        play.setDisable(true);
        stop.setDisable(true);
        pause.setDisable(true);
        fastForward.setDisable(true);
        slowForward.setDisable(true);
        toEnd.setDisable(true);
        toStart.setDisable(true);
    }


    public void openCSV(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Flight CSV File");
        fc.setInitialDirectory(new File("./resources"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "CSV Files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        if(chosenFile!=null){
            timeSeriesPath.setValue(chosenFile.getAbsolutePath());
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void play(){
        if(onPlay!=null)
            onPlay.run();
    }

    public void stop(){
        if(onStop!=null)
            onStop.run();
    }

    public void pause(){
        if(onPause!=null)
            onPause.run();
    }

    public void fastForward(){
        if(onFastForward!=null)
            onFastForward.run();
    }

    public void slowForward(){
        if(onSlowForward !=null)
            onSlowForward.run();
    }

    public void toEnd(){
        if(onToEnd!=null)
            onToEnd.run();
    }

    public void toStart(){
        if(onToStart!=null)
            onToStart.run();
    }
}
