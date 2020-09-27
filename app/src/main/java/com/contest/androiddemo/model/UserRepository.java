package com.contest.androiddemo.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

public class UserRepository {
    @Inject
    MyApplication application;

    @Inject
    Retrofit retrofit;

    public UserRepository(MyApplication application) {
        application.getComponent().inject(this);
    }

    public LiveData<UserData> getUser(String userId) {
        MockWebServer mockWebServer = new MockWebServer();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                //TODO Add your Retrofit parameters here
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mockWebServer.enqueue(new MockResponse().setBody("{\"email\":\"guru3393@gmail.com\",\"avatar_url\":\"avatar_url\"}"));
        final MutableLiveData<UserData> result = new MutableLiveData<>();
        ApiService api = retrofit.create(ApiService.class);
        Call<UserData> call = api.getUser(userId);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                Log.e("Repo",response.toString());
//                Gson gson=new Gson();
//                JSONObject jsonObject = null;
//                try {
//                     jsonObject = new JSONObject("{\"email\":\"guru3393@gmail.com\",\"avatar_url\":\"avatar_url\"}");
//                }catch (JSONException err){
//                    Log.d("Error", err.toString());
//                }
//                UserData name =gson.fromJson(jsonObject.toString(),UserData.class);
                result.postValue(response.body());
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e("Repo",t.getLocalizedMessage());
            }
        });
//        try {
//            mockWebServer.shutdown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return result;
    }
}
