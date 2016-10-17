package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Sevle on 10/16/2016.
 */
public class SimpleResponse {

    @Expose
    private String response;

    @Expose
    private Boolean isOk;


    public SimpleResponse(String s) {
        this.response = s;
    }

    public SimpleResponse(String s,Boolean isOk) {

        this.response = s;
        this.isOk = isOk;
    }


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Boolean getIsOk() {
        return isOk;
    }

    public void setIsOk(Boolean isOk) {
        this.isOk = isOk;
    }
    @Override
    public String toString() {
        return "SimpleResponse{" +
                "response='" + response + '\'' +
                ", isOk=" + isOk +
                '}';
    }
}
