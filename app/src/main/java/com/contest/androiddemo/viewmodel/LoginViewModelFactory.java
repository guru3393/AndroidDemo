package com.contest.androiddemo.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.contest.androiddemo.MyApplication;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MyApplication application;

    public LoginViewModelFactory(MyApplication application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginViewModel(application);
    }
}
