package com.contest.androiddemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.contest.androiddemo.MyApplication;
import com.contest.androiddemo.R;
import com.contest.androiddemo.model.UserData;
import com.contest.androiddemo.viewmodel.ImageViewModel;
import com.contest.androiddemo.viewmodel.ImageViewModelFactory;
import com.contest.androiddemo.viewmodel.UserViewModel;
import com.contest.androiddemo.viewmodel.UserViewModelFactory;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.tkeunebr.gravatar.Gravatar;

public class UserDetails extends AppCompatActivity {

    @Inject
    MyApplication application;
    @Inject
    SharedPreferences sharedPreferences;

    UserViewModel userViewModel;
    ImageViewModel imageviewmodel;
    String userId;
    String token;

    @BindView(R.id.imageview_account_profile)
    ImageView profilepic;
    @BindView(R.id.tvemailid)
    TextView tv_emailid;

    private static final int REQUEST_IMAGE = 100;
    private static final int maxSizeBytes = 1024*1024;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).getComponent().inject(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        userViewModel = ViewModelProviders.of(this, new UserViewModelFactory(application)).get(UserViewModel.class);
        imageviewmodel = ViewModelProviders.of(this, new ImageViewModelFactory(application)).get(ImageViewModel.class);

        userId = sharedPreferences.getString("USER_ID", "default");
        token = sharedPreferences.getString("USER_TOKEN", "default");


        getUserDetails(userId);


    }

    public void getUserDetails(String userId) {
            userViewModel.submit(userId);
            userViewModel.result.observe(this, new Observer<UserData>() {
                @Override
                public void onChanged(@Nullable UserData userdata) {
                    assert userdata != null;
                    if (userdata.getEmail() != null) {
                        Toast.makeText(UserDetails.this, userdata.getEmail(), Toast.LENGTH_SHORT).show();
                        tv_emailid.setText(userdata.getEmail());
                        if (userdata.getUserPicUrl().equals("avatar_url")) {
                            String gravatarUrl = Gravatar.init().with("guru3393@gmail.com").size(Gravatar.MAX_IMAGE_SIZE_PIXEL).build();
                            String aUrl = gravatarUrl.replace("http", "https");
                            Picasso.with(UserDetails.this)
                                    .load(aUrl)
                                    .rotate(90)
                                    .transform(new CircleTransform())
                                    .into(profilepic);
                        }else{

                            Picasso.with(UserDetails.this)
                                    .load(userdata.getUserPicUrl())
                                    .into(profilepic);
                        }
                    } else {
//                        Toast.makeText(UserDetails.this, userdata.getError().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }


    public void edit(View view) {
        showImagePickerOptions();
    }
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(UserDetails.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(UserDetails.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (data != null) {

                    uri = data.getParcelableExtra("path");
                    String imagePath;
                    if (uri.toString().contains("content:")) {
                        imagePath = getRealPathFromURI(uri);
                    } else  {
                        imagePath = uri.getPath();
                    }
                    File f = new File(imagePath);

                    Picasso.with(UserDetails.this)
                            .load(f)
                            .transform(new CircleTransform())
                            .into(profilepic);
                    ImageUpload upload = new ImageUpload(imagePath);
                    upload.execute();
                }

            }
        }
    }
    private void subscribeUploadImage(String imageData) {
        imageviewmodel.uploadImage(userId,imageData);
    }
    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int currSize;
        int currQuality = 100;

        do {
            bitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream);
            currSize = stream.toByteArray().length;
            // limit quality by 5 percent every time
            currQuality -= 5;

        } while (currSize >= maxSizeBytes);

        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null,
                    null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private final class ImageUpload extends AsyncTask<Void, Void, String> {

        private String  mimagePath;
        public ImageUpload(String imagePath) {
            mimagePath = imagePath;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                File f = new File(mimagePath);
                Uri mUri = Uri.fromFile(f);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(UserDetails.this.getContentResolver(), mUri);

                String encodedFile ;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    encodedFile = Base64.getEncoder().encodeToString(compressImage(mimagePath));
                } else {
                    encodedFile = android.util.Base64.encodeToString(compressImage(mimagePath), android.util.Base64.DEFAULT);
                }
                subscribeUploadImage(encodedFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            // You might want to change "executed" for the returned string
            // passed into onPostExecute(), but that is up to you
        }
    }

    public byte[] compressImage(String filePath) {

//        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        int currSize = stream.toByteArray().length;
        scaledBitmap.recycle();
        bmp.recycle();


        return byteArray;

    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}