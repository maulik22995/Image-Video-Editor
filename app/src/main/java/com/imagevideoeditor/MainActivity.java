package com.imagevideoeditor;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.imagevideoeditor.Utils.CameraUtils;
import com.imagevideoeditor.databinding.ActivityMainBinding;
import com.kbeanie.multipicker.api.CameraVideoPicker;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraUtils.OnCameraResult {

    private CameraUtils cameraUtils;
    private ActivityMainBinding activityMainBinding;
    private CameraVideoPicker cameraVideoPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        cameraUtils = new CameraUtils(this, this);

        activityMainBinding.btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUtils.openCameraGallery();
            }
        });
        activityMainBinding.btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                cameraUtils.alertVideoSelcetion();


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        cameraUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSuccess(List<ChosenImage> images) {
        if (images != null && images.size() > 0) {
            Intent i = new Intent(MainActivity.this, PreviewPhotoActivity.class);
            i.putExtra("DATA", images.get(0).getOriginalPath());
            //binding.ivProfilePic.setImageURI(Uri.fromFile(selectedImageFile));
            startActivity(i);

        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraUtils.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onVideoSuccess(List<ChosenVideo> list) {
        if (list != null && list.size() > 0) {
            Intent i = new Intent(MainActivity.this, PreviewVideoActivity.class);
            i.putExtra("DATA", list.get(0).getOriginalPath());
            //binding.ivProfilePic.setImageURI(Uri.fromFile(selectedImageFile));
            startActivity(i);

        }
    }
}

