package view;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public final class applicationStatus {
    private static Label appStatus = new Label();
    private static PauseTransition pause = new PauseTransition();

    public static void setPauseDuration(double duration){ pause.setDuration(Duration.seconds(duration)); }
    public static void setPauseOnFinished(EventHandler<ActionEvent> e){ pause.setOnFinished(e); }
    public static void pausePlayFromStart(){ pause.playFromStart(); }
    public static void setAppStatusValue(String status){ appStatus.textProperty().set(status);}
    public static String getAppStatusValue(){return appStatus.textProperty().get();}
    public static StringProperty getAppStatusStringProperty(){return appStatus.textProperty();}
    public static Label getAppStatus(){return appStatus;}

    public static void setAppColor(Color color){
        appStatus.setTextFill(color);
    }


}
