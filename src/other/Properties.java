package other;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.*;

public class Properties implements Serializable {
    private String ip;
    private Integer port;
    private Integer hertzRate;
    private LinkedHashMap<String,FeatureProperties> map;

    public Properties() {
        map = new LinkedHashMap<>();
    }

    public void createXML(){
        XMLEncoder e = null;
        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream("./resources/properties.xml")));
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

    public LinkedHashMap<String, FeatureProperties> getMap() {
        return map;
    }

    public void setMap(LinkedHashMap<String, FeatureProperties> map) {
        this.map = map;
    }


    public void setDefaultProperties(){
        ip="127.0.0.1";
        port=5400;
        hertzRate=10;
        //Gps
        map.put("latitude", new Properties.FeatureProperties("latitude-deg", Float.valueOf(-90),Float.valueOf(90)));
        map.put("longitude", new Properties.FeatureProperties("longitude-deg", Float.valueOf(-180),Float.valueOf(180)));
        map.put("altitude", new Properties.FeatureProperties("altimeter_indicated-altitude-ft", Float.MIN_VALUE,Float.MAX_VALUE));

        map.put("airspeed", new Properties.FeatureProperties("airspeed-indicator_indicated-speed-kt", Float.valueOf(0),Float.MAX_VALUE));
        map.put("heading", new Properties.FeatureProperties("indicated-heading-deg", Float.valueOf(0),Float.valueOf(359)));
        map.put("roll", new Properties.FeatureProperties("attitude-indicator_indicated-roll-deg", Float.valueOf(-90),Float.valueOf(90)));
        map.put("pitch", new Properties.FeatureProperties("attitude-indicator_internal-pitch-deg", Float.valueOf(-90),Float.valueOf(90)));
        map.put("yaw", new Properties.FeatureProperties("side-slip-deg", Float.valueOf(-90),Float.valueOf(90)));

        map.put("throttle", new Properties.FeatureProperties("throttle", Float.valueOf(0),Float.valueOf(1)));

        map.put("aileron", new Properties.FeatureProperties("aileron", Float.valueOf(-1),Float.valueOf(1)));
        map.put("elevators", new Properties.FeatureProperties("elevator",  Float.valueOf(-1),Float.valueOf(1)));
        map.put("rudder", new Properties.FeatureProperties("rudder",  Float.valueOf(-1),Float.valueOf(1)));
    }

    public boolean isValidProperties(){
        if(ip==null||port==null||hertzRate==null||map==null)
            return false;

        if(!validateIp(ip)||port<1024||port>65535||hertzRate<1)
            return false;

        ArrayList<FeatureProperties> fp = new ArrayList<>();
        for(Map.Entry<String,FeatureProperties> e : map.entrySet())
            fp.add(e.getValue());

        if(!fp.get(0).checkFeatureRanges("latitude-deg",Float.valueOf(-90),Float.valueOf(90)))
            return false;
        if(!fp.get(1).checkFeatureRanges("longitude-deg", Float.valueOf(-180),Float.valueOf(180)))
            return false;
        if(!fp.get(2).checkFeatureRanges("altimeter_indicated-altitude-ft", Float.MIN_VALUE,Float.MAX_VALUE))
            return false;
        if(!fp.get(3).checkFeatureRanges("airspeed-indicator_indicated-speed-kt", Float.valueOf(0),Float.MAX_VALUE))
            return false;
        if(!fp.get(4).checkFeatureRanges("indicated-heading-deg", Float.valueOf(0),Float.valueOf(359)))
            return false;
        if(!fp.get(5).checkFeatureRanges("attitude-indicator_indicated-roll-deg", Float.valueOf(-90),Float.valueOf(90)))
            return false;
        if(!fp.get(6).checkFeatureRanges("attitude-indicator_internal-pitch-deg", Float.valueOf(-90),Float.valueOf(90)))
            return false;
        if(!fp.get(7).checkFeatureRanges("side-slip-deg", Float.valueOf(-90),Float.valueOf(90)))
            return false;
        if(!fp.get(8).checkFeatureRanges("throttle", Float.valueOf(0),Float.valueOf(1)))
            return false;
        if(!fp.get(9).checkFeatureRanges("aileron", Float.valueOf(-1),Float.valueOf(1)))
            return false;
        if(!fp.get(10).checkFeatureRanges("elevator",  Float.valueOf(-1),Float.valueOf(1)))
            return false;
        if(!fp.get(11).checkFeatureRanges("rudder",  Float.valueOf(-1),Float.valueOf(1)))
            return false;

        return true;
    }

    public static boolean validateIp(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
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

        public boolean checkFeatureRanges(String s,Float min,Float max){
            if(!this.colName.equals(s))
                return false;
            if(this.minVal<min||this.minVal>max)
                return false;
            if(this.maxVal<min||this.maxVal>max)
                return false;
            if(this.maxVal<this.minVal)
                return false;
            return true;
        }

    }
}
