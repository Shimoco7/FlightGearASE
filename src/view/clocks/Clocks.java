package view.clocks;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class Clocks extends StackPane {
    public DoubleProperty headingDeg,pitch,roll,altimeter;
    public Clocks() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Clocks.fxml"));
            StackPane clocks = loader.load();
            ClocksController clocksController = loader.getController();
            clocks.setAlignment(Pos.CENTER);
            headingDeg=clocksController.myAirCompass.bearingProperty();
            pitch = clocksController.myHorizon.pitchProperty();
            roll = clocksController.myHorizon.rollProperty();
            altimeter = clocksController.myAltimeter.valueProperty();

            this.getChildren().add(clocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
