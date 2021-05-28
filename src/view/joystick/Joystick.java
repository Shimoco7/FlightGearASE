package view.joystick;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import view.player.PlayerController;

import java.io.IOException;

public class Joystick extends StackPane {
    public Joystick() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Joystick.fxml"));
            StackPane joy = loader.load();
            joy.setAlignment(Pos.CENTER);
            JoystickController joystickController = loader.getController();
            joystickController.vertical.setShowTickMarks(true);
            joystickController.horizontal.setShowTickMarks(true);
            this.getChildren().add(joy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
