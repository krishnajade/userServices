package com.example.userservices.services;

import com.example.userservices.models.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("usr.php?apicall=login")
    Call<String> login(@Field("mobile") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("sereng.php?apicall=read")
    Call<User> getUser(@Field("mobile") String mobileNumber);

    @FormUrlEncoded
    @POST("usr.php?apicall=mobexist")
    Call<String> getUser1(@Field("mobile") String mobileNumber);
}