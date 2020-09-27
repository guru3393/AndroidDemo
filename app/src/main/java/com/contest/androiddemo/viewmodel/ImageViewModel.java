package com.contest.androiddemo.viewmodel;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.contest.androiddemo.MyApplication;
import com.contest.androiddemo.model.ImageRepository;
import com.contest.androiddemo.model.UploadImageResponseModel;

public class ImageViewModel extends AndroidViewModel {

    public LiveData<UploadImageResponseModel> result;

    public ImageRepository userRepository;

    public ImageViewModel(@NonNull MyApplication application) {
        super(application);
        userRepository = new ImageRepository(application);
    }


    public void uploadImage(String userid,String imagedata) {
        Log.e("UserViewModel", "Called");
        result = userRepository.uploadImage(userid,imagedata);
    }
}
