package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by skand on 11/21/2016.
 */

public class DataPacket {
    @Expose
    private ArrayList<AccelerometerStats> accelerometerStats;
    @Expose
    private ArrayList<OrientationStats> orientationStats;
    @Expose
    private User user;

    public DataPacket() {
    }

    public DataPacket(ArrayList<AccelerometerStats> accelerometerStats, ArrayList<OrientationStats> orientationStats, User user) {
        this.accelerometerStats = accelerometerStats;
        this.orientationStats = orientationStats;
        this.user = user;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "accelerometerStats=" + accelerometerStats +
                ", orientationStats=" + orientationStats +
                ", user=" + user.toString() +
                '}';
    }

    public ArrayList<AccelerometerStats> getAccelerometerStats() {
        return accelerometerStats;
    }

    public void setAccelerometerStats(ArrayList<AccelerometerStats> accelerometerStats) {
        this.accelerometerStats = accelerometerStats;
    }

    public ArrayList<OrientationStats> getOrientationStats() {
        return orientationStats;
    }

    public void setOrientationStats(ArrayList<OrientationStats> orientationStats) {
        this.orientationStats = orientationStats;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
