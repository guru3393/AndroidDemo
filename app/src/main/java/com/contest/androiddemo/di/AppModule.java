package com.contest.androiddemo.di;


import com.contest.androiddemo.MyApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private MyApplication mApplication;

    public AppModule(MyApplication application){
        mApplication = application;
    }

    @Provides
    @Singleton
    MyApplication provideApplication(){
        return mApplication;
    }
}
