package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.FGModel;
import viewmodel.ViewModel;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FGModel model = new FGModel();
        ViewModel vm = new ViewModel(model);
        model.addObserver(vm);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = (Parent)loader.load();
        MainWindowController myController = loader.getController();
        myController.setStage(primaryStage);
        primaryStage.setTitle("Flight-Gear Projector");
        primaryStage.setScene(new Scene(root, 700, 557));
        primaryStage.show();

        MainWindowController mwc = loader.getController();
        mwc.setViewModel(vm);
        vm.addObserver(mwc);


    }


    public static void main(String[] args) {
        launch(args);
    }
}
