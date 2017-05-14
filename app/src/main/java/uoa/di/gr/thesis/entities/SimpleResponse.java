package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

public class SimpleResponse {

    @Expose
    private String response;

    @Expose
    private Boolean ok;

    @Expose
    private Boolean elderly;

    public SimpleResponse() {
    }

    public SimpleResponse(String response, Boolean ok) {
        this.response = response;
        this.ok = ok;
    }

    public SimpleResponse(String response, Boolean ok, Boolean elderly) {
        this.response = response;
        this.ok = ok;
        this.elderly = elderly;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public Boolean getElderly() {
        return elderly;
    }

    public void setElderly(Boolean elderly) {
        this.elderly = elderly;
    }

    @Override
    public String toString() {
        return "SimpleResponse{" +
                "response='" + response + '\'' +
                ", ok=" + ok +
                ", elderly=" + elderly +
                '}';
    }
}
