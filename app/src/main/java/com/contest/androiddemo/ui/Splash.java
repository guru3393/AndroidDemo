package com.contest.androiddemo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.contest.androiddemo.MyApplication;

import androidx.appcompat.app.AppCompatActivity;

import com.contest.androiddemo.R;

import javax.inject.Inject;

public class Splash extends AppCompatActivity {

    @Inject
    MyApplication application;

    String token;

    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ((MyApplication) getApplication()).getComponent().inject(this);

        token = sharedPreferences.getString("USER_TOKEN", "default");
        if(token.equals("default")){
            Intent myIntent = new Intent(Splash.this, MainActivity.class);
            Splash.this.startActivity(myIntent);
        }else{
            Intent myIntent = new Intent(Splash.this, UserDetails.class);
            Splash.this.startActivity(myIntent);
        }
    }
}