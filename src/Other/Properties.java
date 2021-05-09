package Other;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class Properties implements Serializable {
    private String ip;
    private Integer port;
    private Integer hertzRate;
    private HashMap<String,FeatureProperties> map;

    public Properties() {
        map = new HashMap<>();
    }

    public void createXML(){
        XMLEncoder e = null;
        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("properties.xml")));
            e.writeObject(this);

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        e.close();

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getHertzRate() {
        return hertzRate;
    }

    public void setHertzRate(Integer hertzRate) {
        this.hertzRate = hertzRate;
    }

    public HashMap<String, FeatureProperties> getMap() {
        return map;
    }

    public void setMap(HashMap<String, FeatureProperties> map) {
        this.map = map;
    }

    public void setDefaultProperties(){
        ip="127.0.0.1";
        port=5400;
        hertzRate=10;
        //Gps
        map.put("Latitude", new Properties.FeatureProperties("latitude-deg", Float.valueOf(-90),Float.valueOf(90)));
        map.put("Longitude", new Properties.FeatureProperties("longitude-deg", Float.valueOf(-180),Float.valueOf(180)));
        map.put("Altitude", new Properties.FeatureProperties("altimeter_indicated-altitude-ft", Float.MIN_VALUE,Float.MAX_VALUE));

        map.put("Airspeed", new Properties.FeatureProperties("airspeed-indicator_indicated-speed-kt", Float.valueOf(0),Float.MAX_VALUE));
        //Yaw and Heading are the same?
        map.put("Heading", new Properties.FeatureProperties("indicated-heading-deg", Float.valueOf(0),Float.valueOf(359)));
        map.put("Roll", new Properties.FeatureProperties("attitude-indicator_indicated-roll-deg", Float.valueOf(-90),Float.valueOf(90)));
        map.put("Pitch", new Properties.FeatureProperties("attitude-indicator_internal-pitch-deg", Float.valueOf(-90),Float.valueOf(90)));
        map.put("Yaw", new Properties.FeatureProperties("side-slip-deg", Float.valueOf(-90),Float.valueOf(90)));

        //2 throttles in Playback-xml
        map.put("Throttle", new Properties.FeatureProperties("throttle", Float.valueOf(0),Float.valueOf(1)));

        map.put("Aileron", new Properties.FeatureProperties("aileron", Float.valueOf(-1),Float.valueOf(1)));
        map.put("Elevators", new Properties.FeatureProperties("elevator",  Float.valueOf(-1),Float.valueOf(1)));
        map.put("Rudder", new Properties.FeatureProperties("rudder",  Float.valueOf(-1),Float.valueOf(1)));
    }

    public static class FeatureProperties implements Serializable{
        String colName;
        Float minVal, maxVal;

        public FeatureProperties() {
        }

        public FeatureProperties(String colName, Float minVal, Float maxVal) {
            this.colName = colName;
            this.minVal = minVal;
            this.maxVal = maxVal;
        }

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public Float getMinVal() {
            return minVal;
        }

        public void setMinVal(Float minVal) {
            this.minVal = minVal;
        }

        public Float getMaxVal() {
            return maxVal;
        }

        public void setMaxVal(Float maxVal) {
            this.maxVal = maxVal;
        }
    }
}
