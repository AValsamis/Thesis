package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class RecognizedActivity implements Serializable {

    @Expose
    private Long id;
    @Expose
    private User user;
    @Expose
    private String timestamp;
    @Expose
    private String state = "";
    @Expose
    private Integer certainty;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getCertainty() {
        return certainty;
    }

    public void setCertainty(Integer certainty) {
        this.certainty = certainty;
    }

    @Override
    public String toString() {
        return "RecognizedActivity{" +
                "id=" + id +
                ", user=" + user +
                ", timestamp='" + timestamp + '\'' +
                ", state='" + state + '\'' +
                ", certainty=" + certainty +
                '}';
    }
}