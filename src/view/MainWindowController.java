package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import model.FGModel;
import viewmodel.ViewModel;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements Observer {

    ViewModel vm;
    ExecutorService executorService;
    Stage stage;
    @FXML
    Label appStatus;



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
            appStatus.setTextFill(Color.RED);
            vm.appStat.setValue("Failed to load resource");
        }
        else{
            vm.setAppProperties(chosenFile.getAbsolutePath());
        }
        cleanStatusBox();

    }

    public void cleanStatusBox(){
        executorService.execute(()->{
            try {
                Thread.sleep(5000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(()->vm.appStat.setValue(""));
        });
    }

    public void setViewModel(ViewModel vm) {
        this.vm = vm;
        executorService = Executors.newSingleThreadExecutor();
        appStatus.textProperty().bind(vm.appStat);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(ViewModel.class)){
            if(arg.toString().equals("Red"))
                appStatus.setTextFill(Color.RED);
            else if(arg.toString().equals("Green"))
                appStatus.setTextFill(Color.GREEN);
            else
                appStatus.setTextFill(Color.BLACK);
        }
    }

}
