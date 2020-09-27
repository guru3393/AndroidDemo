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


public class ImageRepository {
    @Inject
    MyApplication application;

    @Inject
    Retrofit retrofit;

    public ImageRepository(MyApplication application) {
        application.getComponent().inject(this);
    }

    public LiveData<UploadImageResponseModel> uploadImage(String userid,String imageBase64String) {
        MockWebServer mockWebServer = new MockWebServer();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                //TODO Add your Retrofit parameters here
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mockWebServer.enqueue(new MockResponse().setBody("{\"email\":\"guru3393@gmail.com\",\"avatar_url\":\"http://gururaj_3393.jpg\"}"));
        final MutableLiveData<UploadImageResponseModel> result = new MutableLiveData<>();
        ApiService api = retrofit.create(ApiService.class);
        Call<UploadImageResponseModel> call = api.uploadImage(userid,imageBase64String);
        call.enqueue(new Callback<UploadImageResponseModel>() {
            @Override
            public void onResponse(Call<UploadImageResponseModel> call, Response<UploadImageResponseModel> response) {
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
            public void onFailure(Call<UploadImageResponseModel> call, Throwable t) {
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
