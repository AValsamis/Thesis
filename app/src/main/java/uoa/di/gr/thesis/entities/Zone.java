package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Sevle on 9/23/2016.
 */
public class Zone {

    @Expose
    private Long Id;
    @Expose
    private String friendlyName = "";
    @Expose
    private User user;
    // Integer in case we want to include neutral zones in the future
    // 0: dangerous 1: safe (2: neutral)
    @Expose
    private Integer isSafe;

    public Zone(){}

    public Zone(Long id, User user, Integer isSafe, String friendlyName) {
        Id = id;
        this.friendlyName = friendlyName;
        this.user = user;
        this.isSafe = isSafe;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
                "Id=" + Id +
                ", friendlyName='" + friendlyName + '\'' +
                ", user=" + user +
                ", isSafe=" + isSafe +
                '}';
    }
}
