package com.example.userservices.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedbackService {

    @FormUrlEncoded
    @POST("sereng.php?apicall=feedback")
    Call<String> submitFeedback(@Field("usrid") String usrid,@Field("rating")float rating,@Field("UserFeed")String UserFeed);
}