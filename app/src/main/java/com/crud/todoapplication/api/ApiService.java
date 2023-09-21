package com.crud.todoapplication.api;

import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.SignUp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ApiService {
    @POST("api/v1/user/signup/")
    Call<ResponseBody> createUser(@Body SignUp signUp);

    @FormUrlEncoded
    @POST("api/v1/user/login/")
    Call<ResponseBody> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/v1/user/reset/password/")
    Call<ResponseBody> resetPassword(
            @Field("email") String email,
            @Field("password") String password,
            @Field("oldHint") String oldHint,
            @Field("newHint") String newHint
    );

    @GET("api/v1/user/details")
    Call<ResponseBody> getUserDetail();

}

