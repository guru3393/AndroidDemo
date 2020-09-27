package com.contest.androiddemo.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.contest.androiddemo.MyApplication;
import com.contest.androiddemo.model.UserData;
import com.contest.androiddemo.model.UserRepository;

public class UserViewModel extends AndroidViewModel {

    public LiveData<UserData> result;

    private UserRepository userRepository;

    public UserViewModel(@NonNull MyApplication application) {
        super(application);
        userRepository = new UserRepository(application);
    }


    public void submit(String userId) {
        Log.e("UserViewModel", "Called");
        result = userRepository.getUser(userId);
    }
}
