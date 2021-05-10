package com.islam.myapplication.fcm.model;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;


public interface JsonPlaceHolderApi {

    ////-------------------------USER--------------------------
    @POST("user/getwait")
    Call<List<User>> mGetUserWait(@Query("id_center") int id);
    @POST("user/getone")
    Call<List<User>> mGetUser(@Query("email") String email);
    @GET("user/getall")
    Call<List<User>> mGetUsers();
    @POST("user/storeone")
    Call<String> mPostUser(@Body User user);
    @PUT("user/editeone")
    Call<User> mEditeUser(@Body User user);
    @DELETE("user/deleteone")
    Call<String> mDeleteUser(@Query("id")int id);
    @POST("user/getcenter")
    Call<List<User>> mGetUserOneCenter(@Query("id_center") int id);
 //----------------------------convert
 @POST("user/convert")
 Call<String> convert(@Body List<User> users);

    @PUT("user/editetoken")
    Call<User> editetoken(@Body User user);
    ////-------------------------CENTER--------------------------
    @POST("center/getone")
    Call<List<Center>> mGetCenter(@Query("id") int id);
    @GET("center/getall")
    Call<List<Center>> mGetCenters();
    @POST("center/storeone")
    Call<String> mPostCenter(@Body Center center);
    @PUT("center/editeone")
    Call<List<User>> mEditeCenter(@Body Center center);
    @DELETE("center/deleteone")
    Call<String> mDeleteCenter(@Query("id")int id);

}
