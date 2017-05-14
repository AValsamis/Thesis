package uoa.di.gr.thesis.entities;

import com.google.gson.annotations.Expose;

public class User {

    @Expose
    private Long id;
    @Expose
    private String username = "";
    @Expose
    private String name = "";
    @Expose
    private String surname = "";
    @Expose
    private String password = "";

    private String responsibleUserName;

    @Expose
    private String token= "";
    public User(){}

    public User(String username, String name, String surname, String password, String responsibleUserName) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.responsibleUserName = responsibleUserName;

    }

    public User(String username, String name, String surname, String password, String responsibleUserName, String token) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.responsibleUserName = responsibleUserName;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getResponsibleUserName() {
        return responsibleUserName;
    }

    public void setResponsibleUserName(String responsibleUserName) {
        this.responsibleUserName = responsibleUserName;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", responsibleUserName='" + responsibleUserName +
                '}';
    }
}
