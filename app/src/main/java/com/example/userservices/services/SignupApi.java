package com.example.userservices.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SignupApi {
    @FormUrlEncoded
    @POST("usr.php?apicall=signup")
    Call<String> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("gender") String gender,
            @Field("mobile") String mobile,
            @Field("created_location")String created_location,
            @Field("status")String status);

    @FormUrlEncoded
    @POST("usr.php?apicall=regist")
    Call<String> signup1(
            @Field("mobile") String mobile,
            @Field("FName") String FName,
            @Field("MName") String MName,
            @Field("LName") String LName,
            @Field("Address") String Address,
            @Field("City")String city,
            @Field("State")String state,
            @Field("PAN_No")String PAN_NO,
            @Field("Aadhar_No")String Aadhar_No,
            @Field("status")String status,
            @Field("created_location")String created_location,
            @Field("Occupation")String Occupation
    );

    @FormUrlEncoded
    @POST("sereng.php?apicall=readmob")
    Call<String> getMobileNumber(@Field("username") String username);

//    @FormUrlEncoded
//    @POST("usr.php?apicall=update")
//    Call<Void> updateStatus(@Field("mobile")String username);

//    @FormUrlEncoded
//    @POST("sereng.php?apicall=OK")
//    Call<Void> updateStatusok(@Field("username")String username);
}