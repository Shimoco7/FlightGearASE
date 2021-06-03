package view.joystick;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

public class JoystickController implements Initializable {
    @FXML
    SpaceXJoystick joystick;
    @FXML
    Slider vertical;
    @FXML
    Slider horizontal;

    public JoystickController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        joystick.yProperty().addListener(e-> joystick.paint());
        joystick.xProperty().addListener(e-> joystick.paint());

    }


}