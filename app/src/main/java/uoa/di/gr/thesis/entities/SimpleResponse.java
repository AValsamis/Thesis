package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Sevle on 10/16/2016.
 */
public class SimpleResponse {

    @Expose
    private String response;


    public SimpleResponse(String s) {
        this.response = s;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    @Override
    public String toString() {
        return "SimpleResponse{" +
                "response='" + response + '\'' +
                '}';
    }
}
