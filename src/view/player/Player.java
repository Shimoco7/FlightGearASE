package view.player;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.Main;

import java.io.IOException;

public class Player extends AnchorPane {

    public Player() {
        super();
        FXMLLoader loader = new FXMLLoader();
        try {
            AnchorPane player = loader.load((getClass().getResource("Player.fxml")));
            PlayerController playerController = loader.getController();
            this.getChildren().add(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
