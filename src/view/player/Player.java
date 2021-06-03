package view.player;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import view.Main;

import java.io.IOException;

public class Player extends AnchorPane {

    public final PlayerController controller;

    public Player() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Player.fxml"));
        AnchorPane player=null;
        try {
            player = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(player!=null){
            controller = loader.getController();
            player.setLayoutX(180);
            this.getChildren().add(player);
        }
        else{
            controller = null;
        }
    }
}
