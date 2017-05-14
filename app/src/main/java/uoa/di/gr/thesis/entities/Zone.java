package uoa.di.gr.thesis.entities;


import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class Zone implements Comparable<Zone>{

    @Expose
    private Long zoneId;
//    @Expose
//    private String zoneId = "";
    @Expose
    private String friendlyName = "";
//    @Expose
//    private Wifi wifi;
//    @Expose
//    private Double signalStrength;
    @Expose
    private User user;
    // Integer in case we want to include neutral zones in the future
    // 0: dangerous 1: safe (2: neutral)
    @Expose
    private Integer isSafe;

    public Zone(){}

    public Zone(Long zoneId, User user, Integer isSafe, String friendlyName) {
        this.zoneId = zoneId;
        this.friendlyName = friendlyName;
        this.user = user;
        this.isSafe = isSafe;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

//    public String getZoneId() {
//        return zoneId;
//    }
//
//    public void setZoneId(String zoneId) {
//        this.zoneId = zoneId;
//    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

//    public Wifi getWifi() {
//        return wifi;
//    }
//
//    public void setWifi(Wifi wifi) {
//        this.wifi = wifi;
//    }
//
//    public Double getSignalStrength() {
//        return signalStrength;
//    }
//
//    public void setSignalStrength(Double signalStrength) {
//        this.signalStrength = signalStrength;
//    }

    public Integer getIsSafe() {
        return isSafe;
    }

    public void setIsSafe(Integer isSafe) {
        this.isSafe = isSafe;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zoneId=" + zoneId +
//                ", zoneId='" + zoneId + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
//                ", wifi=" + wifi +
//                ", signalStrength=" + signalStrength +
                ", user=" + user +
                ", isSafe=" + isSafe +
                '}';
    }

    @Override
    public int compareTo(@NonNull Zone another) {
        return another.getZoneId().intValue()-this.getZoneId().intValue();
    }
}
