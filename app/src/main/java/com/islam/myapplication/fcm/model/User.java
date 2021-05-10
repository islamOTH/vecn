package com.islam.myapplication.fcm.model;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class User   {
    //  'id',
    @SerializedName("id")
    private int id;
    //        'name',
    @SerializedName("name")
    private String name;
    //        'password',
    @SerializedName("password")
    private String password;
    //        'email',
    @SerializedName("email")
    private String email;
    //        'old',
    @SerializedName("old")
    private int old;
    //        'naid',
    @SerializedName("naid")
    private String naid;
    //        'phone',
    @SerializedName("phone")
    private String phone;
    //        'id_center',
    @SerializedName("id_center")
    private int id_center;
    //        'first',
    @SerializedName("first")
    private int first;
    //        'status',
    @SerializedName("status")
    private String status;
    //        'token'
    @SerializedName("token")
    private String token;



    public User(int id, String name, String password, String email, int old, String naid, String phone, int id_center, int first, String status, String token) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.old = old;
        this.naid = naid;
        this.phone = phone;
        this.id_center = id_center;
        this.first = first;
        this.status = status;
        this.token = token;
    }

    public User(String name, String password, String email, String phone, int id_center, int old, String naid) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.id_center=id_center;
        this.first=0;
        this.naid=naid;
        this.status="wait";
        this.old=old;
        token=FirebaseInstanceId.getInstance().getToken();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getOld() {
        return old;
    }

    public void setOld(int old) {
        this.old = old;
    }

    public String getNaid() {
        return naid;
    }

    public void setNaid(String naid) {
        this.naid = naid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId_center() {
        return id_center;
    }

    public void setId_center(int id_center) {
        this.id_center = id_center;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
