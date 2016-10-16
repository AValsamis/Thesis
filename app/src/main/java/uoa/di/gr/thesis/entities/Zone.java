package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Sevle on 9/23/2016.
 */
public class Zone {

    @Expose
    private Long Id;
    @Expose
    private String zoneId = "";
    @Expose
    private Wifi wifi;
    @Expose
    private Double signalStrength;
    @Expose
    private User user;

    public Zone(){}

    public Zone(Wifi wifi, Double signalStrength) {
        this.wifi = wifi;
        this.signalStrength = signalStrength;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Wifi getWifi() {
        return wifi;
    }

    public void setWifi(Wifi wifi) {
        this.wifi = wifi;
    }

    public Double getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Double signalStrength) {
        this.signalStrength = signalStrength;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "Id=" + Id +
                ", zoneId='" + zoneId + '\'' +
                ", wifi=" + wifi +
                ", signalStrength=" + signalStrength +
                ", user=" + user +
                '}';
    }
}
