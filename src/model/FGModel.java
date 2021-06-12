package model;

import javafx.beans.property.IntegerProperty;
import ptm1.*;
import other.Properties;
import ptm1.TimeSeries;
import ptm1.TimeSeriesAnomalyDetector;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class FGModel extends Observable implements Model{

    Properties appProperties;
    IntegerProperty timeStep;
    TimeSeries ts;
    TimeSeries test = new TimeSeries("./resources/anomaly_flight.csv");
    FGPlayer fgp;
    Timer t;
    int hertzRate;
    playSpeed ps;

    public FGModel() {
        setProperties("./resources/properties.xml");
        fgp = new FGPlayer(appProperties);
        hertzRate= appProperties.getHertzRate();
        ps = playSpeed.NORMAL;
    }

    @Override
    public void start() {

    }

    @Override
    public void setTimeSeries(TimeSeries ts) {
        this.ts = ts;
    }

    @Override
    public void setProperties(String path) {
        XMLDecoder d = null;
        try {
            d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream(path)));
        } catch (FileNotFoundException e) {
           e.printStackTrace();
            setChanged();
            notifyObservers("FileNotFound");
        }
        try {
            Properties tempProperties =(Properties) d.readObject();
            if(!tempProperties.isValidProperties()){
                setChanged();
                notifyObservers("IllegalValues");
            }
            else{
                appProperties = tempProperties;
                setChanged();
				notifyObservers("LoadedSuccessfully");
                notifyObservers(appProperties);
            }
        }
        catch(Exception e){
            setChanged();
            notifyObservers("XMLFormatDamaged");
        }
        d.close();

        XMLEncoder e = null;
        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("./resources/properties.xml")));
            e.writeObject(appProperties);

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        e.close();
    }

    @Override
    public void setTimeStep(IntegerProperty timeStep) {
        this.timeStep = timeStep;
    }

    @Override
    public <V> Properties getProperties() {
        return appProperties;
    }

	@Override
	public void setAnomalyDetector(String path) {
		Object detectAlgo = null;
		String className;
		File file = new File(path);
		if (file == null) {
			setChanged();
			notifyObservers("FailedToLoadClass");
		}

		// load class directory
		 className = file.getParentFile().getName() + "." + file.getName().substring(0,file.getName().indexOf("."));
		URL[] url = new URL[1];
		// change this path to your local one
		try {
			url[0] = new URL("file://" + file.getParentFile().getParent() + "/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLClassLoader urlClassLoader = new URLClassLoader(url);
		Class<?> c;
		try {
			c = urlClassLoader.loadClass(className);
			try {
				detectAlgo = c.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// create an Algorithms instance

		setChanged();
		notifyObservers("LoadedClassSuccessfully");

		if (detectAlgo instanceof LineRegressionAnomalyDetector) {
			((LineRegressionAnomalyDetector) detectAlgo).learnNormal(ts);
			((LineRegressionAnomalyDetector) detectAlgo).detect(test);
		}

		if (detectAlgo instanceof HybridAnomalyDetector) {
			((HybridAnomalyDetector) detectAlgo).learnNormal(ts);
			((HybridAnomalyDetector) detectAlgo).detect(test);
		}

		if (detectAlgo instanceof ZscoreAnomalyDetector) {
			((ZscoreAnomalyDetector) detectAlgo).learnNormal(ts);
			((ZscoreAnomalyDetector) detectAlgo).detect(test);
		}

	}

    @Override
    public void play() {
        if(t==null){
            t= new Timer();
            setPlaySpeed();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(timeStep.get()<ts.getRowSize()-1) {
                        timeStep.set(timeStep.get() + 1);
                    }
                    else if(timeStep.get()==ts.getRowSize()-1){
                        t.cancel();
                        t=null;
                    }
                }
            }, 0, hertzRate * 10);
        }
    }

    @Override
    public void skipToStart() {
       stop();
       play();
    }

    @Override
    public void skipToEnd() {
       if(t!=null){
           t.cancel();
           t=null;
       }
       timeStep.set(ts.getRowSize()-1);
    }

    @Override
    public void fastForward() {

            if(ps==playSpeed.SLOWEST){
                ps=playSpeed.SLOWER;
                setChanged();
                notifyObservers(playSpeed.SLOWER);
            }
            else if(ps==playSpeed.SLOWER){
                ps=playSpeed.NORMAL;
                setChanged();
                notifyObservers(playSpeed.NORMAL);
            }
            else if(ps==playSpeed.NORMAL){
                ps=playSpeed.FASTER;
                setChanged();
                notifyObservers(playSpeed.FASTER);
            }
            else if(ps==playSpeed.FASTER){
                ps=playSpeed.FASTEST;
                setChanged();
                notifyObservers(playSpeed.FASTEST);
            }

        if(t!=null) {
            t.cancel();
            t = null;
            play();
        }
    }


    @Override
    public void slowForward() {
            if(ps==playSpeed.SLOWER){
                ps=playSpeed.SLOWEST;
                setChanged();
                notifyObservers(playSpeed.SLOWEST);
            }
            else if(ps==playSpeed.NORMAL){
                ps=playSpeed.SLOWER;
                setChanged();
                notifyObservers(playSpeed.SLOWER);
            }
            else if(ps==playSpeed.FASTEST){
                ps=playSpeed.FASTER;
                setChanged();
                notifyObservers(playSpeed.FASTER);
            }
            else if(ps==playSpeed.FASTER){
                ps=playSpeed.NORMAL;
                setChanged();
                notifyObservers(playSpeed.NORMAL);
            }

        if(t!=null) {
            t.cancel();
            t = null;
            play();
        }
    }


    @Override
    public void pause() {
        if(t!=null){
            t.cancel();
            t=null;
        }
    }

    @Override
    public void stop() {
        if(t!=null){
            t.cancel();
            t=null;
        }
        timeStep.set(0);
        ps=playSpeed.NORMAL;
        setChanged();
        notifyObservers(playSpeed.NORMAL);
    }

    @Override
    public String uploadCsv(String nv) {
        if(nv.equals("")){
            return "";
        }

        HashSet<String> set = new HashSet<>();
        HashMap<String, Properties.FeatureProperties> map = appProperties.getMap();
        HashMap<Integer,String> indexToFeature = new HashMap<>();

        for(Map.Entry<String, Properties.FeatureProperties> entry : map.entrySet()){
            set.add(entry.getValue().getColChosenName());
            indexToFeature.put(entry.getValue().getIndex(),entry.getKey());
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(nv)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = scanner.nextLine();
        String[] features= line.split(",");
        for(String feature: features){
            if(set.contains(feature)){
                set.remove(feature);
            }
        }

        if(set.size()!=0)
            return "missingProperties";

        while(scanner.hasNext()){
            features = scanner.next().split(",");
            for(int i=0;i<features.length;i++){
                try{
                    if(indexToFeature.containsKey(i)) {
                        if (!checkFeatureRanges(indexToFeature.get(i), features[i])) {
                            return "dataOutOfRange";
                        }
                    }
                    Double.parseDouble(features[i]);
                }
                catch (NumberFormatException e){
                    return "incorrectFormat";
                }
            }
        }
        scanner.close();

        return "LoadedCSVSuccessfully";
    }

    private boolean checkFeatureRanges(String s, String data) {
        Properties.FeatureProperties fp = appProperties.getMap().get(s);
        double value = Double.parseDouble(data);
        if(value<fp.getMinVal()||value> fp.getMaxVal())
            return false;
        return true;
    }

    public Properties getAppProperties() {
        return appProperties;
    }

    private void setPlaySpeed(){
        switch(ps){
            case SLOWEST:{
                hertzRate = appProperties.getHertzRate()*4;
                break;
            }
            case SLOWER:{
                hertzRate=appProperties.getHertzRate()*2;
                break;
            }
            case NORMAL: {
                hertzRate = appProperties.getHertzRate();
                break;
            }
            case FASTER:{
                if(hertzRate>2)
                    hertzRate = appProperties.getHertzRate()/2;
                break;
            }
            case FASTEST:{
                if(hertzRate>4)
                    hertzRate = appProperties.getHertzRate()/4;
                break;
            }
        }
    }

    public enum playSpeed{
        SLOWEST,
        SLOWER,
        NORMAL,
        FASTER,
        FASTEST
    }
}
