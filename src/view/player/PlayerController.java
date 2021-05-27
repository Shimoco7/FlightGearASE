package view.player;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.Main;
import view.applicationStatus;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML
    TextField playSpeed;
    @FXML
    Label flightTime;
    Stage stage;
    public StringProperty timeSeriesPath;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = Main.getGuiStage();
        timeSeriesPath = new SimpleStringProperty();
    }


    public void openCSV(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Flight CSV File");
        fc.setInitialDirectory(new File("./resources"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "CSV Files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        if(chosenFile==null){
            applicationStatus.setAppColor(Color.RED);
            applicationStatus.setAppStatusValue("Failed to load resource");
        }
        else{
            timeSeriesPath.set(chosenFile.getAbsolutePath());
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
