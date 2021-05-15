package view;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import viewmodel.ViewModel;

import java.io.File;

public class MainWindowController {

    ViewModel vm;
    Stage stage;

    public void loadProperties(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Project Properties");
        fc.setInitialDirectory(new File("./resources"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "XML Files (*.xml)", "*.xml");
        fc.getExtensionFilters().add(extensionFilter);
        File chosenFile = fc.showOpenDialog(stage);

        //CONTINUE HERE
        if(chosenFile==null){

        }

    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
