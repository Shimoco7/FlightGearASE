package view.clocks;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import view.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class ClocksController implements Initializable {
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = Main.getGuiStage();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}