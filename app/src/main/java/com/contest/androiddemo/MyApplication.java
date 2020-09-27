package com.contest.androiddemo;

import android.app.Application;

import com.contest.androiddemo.di.ApiModule;
import com.contest.androiddemo.di.AppModule;
import com.contest.androiddemo.di.DaggerMainComponent;
import com.contest.androiddemo.di.MainComponent;
import com.contest.androiddemo.di.SharedPrefsModule;

public class MyApplication extends Application {
    MainComponent component;
    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .sharedPrefsModule(new SharedPrefsModule())
                .apiModule(new ApiModule("https://jsonplaceholder.typicode.com"))
                .build();
    }

    public MainComponent getComponent() {
        return component;
    }
}
