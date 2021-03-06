package view.display;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class Display extends StackPane {

    public final DisplayController controller;
    public Display() {
        super();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Display.fxml"));
            StackPane displayer=null;
            try {
                displayer = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

        if(displayer!=null){
            controller = loader.getController();
            displayer.setAlignment(Pos.CENTER);
            this.getChildren().add(displayer);
        }
        else{
            controller = null;
        }
    }
}
