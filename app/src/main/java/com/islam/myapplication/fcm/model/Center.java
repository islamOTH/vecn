package com.islam.myapplication.fcm.model;

import com.google.gson.annotations.SerializedName;

public class Center {
    public static final String ID="ID";
    public static final String NAME="NAME";
    public static final String PASSWORD="PASSWORD";
    public static final String ADDRESS="ADDRESS";
    public static final String TOKEN="TOKEN";

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
    @SerializedName("address")
    private String address;
    @SerializedName("token")
    private String token;
    public Center(int id, String name, String password, String address, String token) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.address = address;
        this.token = token;
    }
    public Center(String name, String password, String address, String token) {
        this.name = name;
        this.password = password;
        this.address = address;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
