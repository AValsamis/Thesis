package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Angelos on 6/19/2016.
 */

public class AccelerometerStats {

    @Expose
    private String x = "";

    @Expose
    private String y = "";

    @Expose
    private String z = "";

    @Expose
    private String timeStamp = "";

    public AccelerometerStats() {
    }

    public AccelerometerStats(String x, String y, String z, String timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "AccelerometerStats{" +
                "x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", z='" + z + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
