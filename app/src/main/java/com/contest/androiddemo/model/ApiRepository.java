package com.contest.androiddemo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.contest.androiddemo.MyApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRepository {

    @Inject
    MyApplication application;

    @Inject
    Retrofit retrofit;

    public ApiRepository(MyApplication application) {
        application.getComponent().inject(this);
    }

    public LiveData<NetworkResponse> getPost(String email,String password) {
        MockWebServer mockWebServer = new MockWebServer();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                //TODO Add your Retrofit parameters here
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mockWebServer.enqueue(new MockResponse().setBody("{\"userid\":\"3393\",\"token\":\"token\"}"));
        final MutableLiveData<NetworkResponse> result = new MutableLiveData<>();
        ApiService api = retrofit.create(ApiService.class);
        Call<LoginResponse> call = api.getPost(email,password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.e("Repo",response.toString());
                LoginResponse repon = response.body();
                result.postValue(new NetworkResponse(response.body()));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Repo",t.getLocalizedMessage());
                result.postValue(new NetworkResponse(t));
            }
        });

        return result;
    }
}
