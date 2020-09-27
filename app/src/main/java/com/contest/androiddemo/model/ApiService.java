package com.contest.androiddemo.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/sessions/new")
    Call<LoginResponse> getPost(@Query("email") String email,@Query("password") String password);

    @GET("/users/{userid}")
    Call<UserData> getUser(@Path("userid") String id);

    @POST("/users/{userid}/avatar")
    Call<UploadImageResponseModel> uploadImage(@Path("userid") String id,@Query("“avatar”:") String base64Imagedata);

}
