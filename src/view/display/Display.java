package view.display;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import view.player.PlayerController;

import java.io.IOException;

public class Display extends StackPane {
    public ObservableList<String> propList;
    public Display() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Display.fxml"));
            StackPane display = loader.load();
            display.setAlignment(Pos.CENTER);
            DisplayController displayController = loader.getController();
            propList = displayController.list.getItems();
            this.getChildren().add(display);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
