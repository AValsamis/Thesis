package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by skand on 11/21/2016.
 */

public class OrientationStats {
    @Expose
    private String azimut = "";

    @Expose
    private String pitch = "";

    @Expose
    private String roll = "";

    @Expose
    private String timeStamp = "";

    public OrientationStats() {
    }

    public OrientationStats(String azimut, String pitch, String roll, String timeStamp) {
        this.azimut = azimut;
        this.pitch = pitch;
        this.roll = roll;
        this.timeStamp = timeStamp;
    }

    public String getAzimut() {
        return azimut;
    }

    public void setAzimut(String azimut) {
        this.azimut = azimut;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "OrientationStats{" +
                "azimut='" + azimut + '\'' +
                ", pitch='" + pitch + '\'' +
                ", roll='" + roll + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
