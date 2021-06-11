package view.clocks;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import view.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class ClocksController implements Initializable {
    Stage stage;
    @FXML Altimeter myAltimeter;
    @FXML AirCompass myAirCompass;
    @FXML Horizon myHorizon;
    @FXML Gauge yaw,airspeed;

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
