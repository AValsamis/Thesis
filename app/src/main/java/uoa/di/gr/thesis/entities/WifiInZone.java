package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

public class WifiInZone {

    @Expose
    private Wifi wifi;

    @Expose
    private Zone zone;

    @Expose
    private Integer singalStrength;

    public Wifi getWifi() {
        return wifi;
    }

    public void setWifi(Wifi wifi) {
        this.wifi = wifi;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Integer getSingalStrength() {
        return singalStrength;
    }

    public void setSingalStrength(Integer singalStrength) {
        this.singalStrength = singalStrength;
    }

    @Override
    public String toString() {
        return "WifiInZone{" +
                "wifi=" + wifi +
                ", zone=" + zone +
                ", singalStrength='" + singalStrength +
                '}';
    }
}
