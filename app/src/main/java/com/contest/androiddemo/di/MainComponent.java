package com.contest.androiddemo.di;

import com.contest.androiddemo.model.ImageRepository;
import com.contest.androiddemo.model.UserRepository;
import com.contest.androiddemo.ui.MainActivity;
import com.contest.androiddemo.model.ApiRepository;
import com.contest.androiddemo.ui.Splash;
import com.contest.androiddemo.ui.UserDetails;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, SharedPrefsModule.class, ApiModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
    void inject(UserDetails activity);
    void inject(ApiRepository apiRespository);

    void inject(UserRepository userRepository);

    void inject(ImageRepository imageRepository);

    void inject(Splash splash);
}
