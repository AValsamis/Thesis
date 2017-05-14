package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DataPacket {
    @Expose
    private ArrayList<AccelerometerStats> accelerometerStats;
    @Expose
    private User user;

    public DataPacket() {
    }

    public DataPacket(ArrayList<AccelerometerStats> accelerometerStats, User user) {
        this.accelerometerStats = accelerometerStats;
        this.user = user;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "accelerometerStats=" + accelerometerStats +
                ", user=" + user.toString() +
                '}';
    }

    public ArrayList<AccelerometerStats> getAccelerometerStats() {
        return accelerometerStats;
    }

    public void setAccelerometerStats(ArrayList<AccelerometerStats> accelerometerStats) {
        this.accelerometerStats = accelerometerStats;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
