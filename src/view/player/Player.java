package view.player;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Player extends AnchorPane {

    public StringProperty timeSeriesPath;

    public Player() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Player.fxml"));
            AnchorPane player = loader.load();
            player.setLayoutX(100);
            PlayerController playerController = loader.getController();
            timeSeriesPath = playerController.timeSeriesPath;

            this.getChildren().add(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
