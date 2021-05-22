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
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        setWindowSize(primaryStage,800,600,600,600);

        MainWindowController mwc = loader.getController();
        mwc.setViewModel(vm);
        vm.addObserver(mwc);


    }


    public static void main(String[] args) {
        launch(args);
    }

    public void setWindowSize(Stage stage,int maxW,int minW, int maxH,int minH){
        stage.widthProperty().addListener((o, oldValue, newValue)->{
            if(newValue.intValue() < minW) {
                stage.setResizable(false);
                stage.setWidth(minW);
                stage.setResizable(true);
            }
            if(newValue.intValue() > maxW) {
                stage.setResizable(false);
                stage.setWidth(maxW);
                stage.setResizable(true);
            }
        });

        stage.heightProperty().addListener((o,oldValue,newValue)->{
            if(newValue.intValue()<minH){
                stage.setResizable(false);
                stage.setHeight(minH);
                stage.setResizable(true);
            }

            if(newValue.intValue() > maxH) {
                stage.setResizable(false);
                stage.setHeight(maxH);
                stage.setResizable(true);
            }
        });
    }
}
