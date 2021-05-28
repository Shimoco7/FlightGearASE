package view.clocks;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class Clocks extends StackPane {

    public Clocks() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Clocks.fxml"));
            Parent root = (Parent)loader.load();
            StackPane clocks = loader.load(getClass().getResource("Clocks.fxml"));
            ClocksController clocksController = loader.getController();
            clocks.setAlignment(Pos.CENTER);

            this.getChildren().add(clocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
