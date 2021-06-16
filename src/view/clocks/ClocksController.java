package view.clocks;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import view.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class ClocksController {
    @FXML Altimeter myAltimeter;
    @FXML AirCompass myAirCompass;
    @FXML Horizon myHorizon;
    @FXML Gauge yaw,airspeed;
}
