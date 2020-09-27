package com.contest.androiddemo.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.contest.androiddemo.MyApplication;

public class ImageViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private MyApplication application;

    public ImageViewModelFactory(MyApplication application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ImageViewModel(application);
    }
}