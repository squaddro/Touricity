package com.squadro.touricity.message.types;

public class Credential {

    private String user_name;
    private String token;

    public Credential() {
        // nothing
    }

    public Credential(String user_name, String token) {
        this.user_name = user_name;
        this.token = token;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name() {
        this.user_name = user_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken() {
        this.token = token;
    }
}