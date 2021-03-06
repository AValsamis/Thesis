package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Wifi {

    @Expose
    private Long wifiId;
    @Expose
    private String macAddress = "";
    @Expose
    private String name = "";
    @Expose
    private ArrayList<Double> signalStrength;

    public Wifi() {
    }

    public Wifi(Long wifiId, String macAddress, String name) {
        this.wifiId = wifiId;
        this.macAddress = macAddress;
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWifiId() {
        return wifiId;
    }

    public void setWifiId(Long wifiId) {
        this.wifiId = wifiId;
    }

    public ArrayList<Double> getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(ArrayList<Double> signalStrength) {
        this.signalStrength = signalStrength;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "wifiId=" + wifiId +
                ", macAddress='" + macAddress + '\'' +
                ", name='" + name + '\'' +
                ", signalStrength=" + signalStrength +
                '}';
    }
}
