package com.contest.androiddemo.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import android.util.Log;

import com.contest.androiddemo.MyApplication;
import com.contest.androiddemo.model.ApiRepository;
import com.contest.androiddemo.model.NetworkResponse;

public class LoginViewModel extends AndroidViewModel {

    public LiveData<NetworkResponse> result;

    private ApiRepository apiRepository;

    public LoginViewModel(@NonNull MyApplication application) {
        super(application);
        apiRepository = new ApiRepository(application);
    }

    public boolean isDetailValid(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void submit(String email, String password) {
        Log.e("LoginViewModel","Called");
        result = apiRepository.getPost(email,password);
    }
}
