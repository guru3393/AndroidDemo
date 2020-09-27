package com.contest.androiddemo.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.contest.androiddemo.MyApplication;
import com.contest.androiddemo.R;
import com.contest.androiddemo.model.NetworkResponse;
import com.contest.androiddemo.viewmodel.LoginViewModel;
import com.contest.androiddemo.viewmodel.LoginViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    @Inject
    MyApplication application;

    @Inject
    SharedPreferences sharedPreferences;

    LoginViewModel loginViewModel;

    @BindView(R.id.emailText)
    EditText emailText;
    @BindView(R.id.passwordText)
    EditText passwordText;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).getComponent().inject(this);



        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(application)).get(LoginViewModel.class);
    }


    public void submit(View view) {
        if (loginViewModel.isDetailValid(emailText.getText().toString(), passwordText.getText().toString())) {
            progressBar.setVisibility(View.VISIBLE);
            loginViewModel.submit(emailText.getText().toString(), passwordText.getText().toString());
            loginViewModel.result.observe(this, new Observer<NetworkResponse>() {
                @Override
                public void onChanged(@Nullable NetworkResponse networkResponse) {
                    progressBar.setVisibility(View.GONE);
                    assert networkResponse != null;
                    if (networkResponse.getPostData() != null) {
//                        Toast.makeText(MainActivity.this, networkResponse.getPostData().getBody(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_ID", networkResponse.getPostData().getUserid().toString().trim());
                        editor.putString("USER_TOKEN", networkResponse.getPostData().getToken().toString().trim());
                        editor.apply();

                        Intent myIntent = new Intent(MainActivity.this, UserDetails.class);
                        MainActivity.this.startActivity(myIntent);
                    } else {
//                        Toast.makeText(MainActivity.this, networkResponse.getError().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        Intent myIntent = new Intent(MainActivity.this, UserDetails.class);
                        MainActivity.this.startActivity(myIntent);
                    }
                }
            });
        }
    }
}
