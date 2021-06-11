package view.display;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import other.Calculate;


import java.net.URL;
import java.util.ResourceBundle;


public class DisplayController implements Initializable {

    public @FXML ListView list;
    public @FXML LineChart leftGraph,rightGraph;
    public @FXML ScatterChart mainGraph;
    XYChart.Series series;


    public DisplayController() { }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        series = new XYChart.Series();
        leftGraph.setAnimated(false);
        rightGraph.setAnimated(false);

        list.getSelectionModel().selectedItemProperty().addListener(e->{
            leftGraph.getData().clear();
        });

    }

    public void display(ObservableList<Float> listItem) {
       leftGraph.getData().clear();
       series.getData().clear();
        for(int i=0;i<listItem.size();i++){
            leftGraph.getData().add(new XYChart.Data<>(getTimeByIndex(i),listItem.get(i)));
        }

        leftGraph.getData().add(series);
    }

    private String getTimeByIndex(int index){
        int timeInSeconds = index/10;
        return Calculate.getTimeString(timeInSeconds);
    }
}
