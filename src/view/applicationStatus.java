package view;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;


public final class applicationStatus {
    private static StringProperty appStatus = new SimpleStringProperty();
    private static PauseTransition pause = new PauseTransition();

    public static void setPauseDuration(double duration){ pause.setDuration(Duration.seconds(duration)); }
    public static void setPauseOnFinished(EventHandler<ActionEvent> e){ pause.setOnFinished(e); }
    public static void pausePlayFromStart(){ pause.playFromStart(); }
    public static void setAppStatus(String status){ appStatus.set(status);}
    public static String getAppStatus(){return appStatus.get();}
    public static StringProperty getAppStatusProperty(){return appStatus;}


}
