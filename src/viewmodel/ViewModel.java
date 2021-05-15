package viewmodel;

import model.Model;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    Model m;



    @Override
    public void update(Observable o, Object arg) {

    }
}
