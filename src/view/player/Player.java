package view.player;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.Main;

import java.io.IOException;
import java.net.URL;

public class Player extends AnchorPane {

    public StringProperty timeSeriesPath;

    public Player() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Player.fxml"));
            Parent root = (Parent)loader.load();
            AnchorPane player = loader.load(getClass().getResource("Player.fxml"));
            player.setLayoutX(100);
            PlayerController playerController = loader.getController();
            timeSeriesPath = playerController.timeSeriesPath;

            this.getChildren().add(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
