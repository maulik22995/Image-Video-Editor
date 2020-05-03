package com.imagevideoeditor.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.imagevideoeditor.R;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.CameraVideoPicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;


import java.util.List;

public class CameraUtils implements ImagePickerCallback, VideoPickerCallback {

    private Activity activity;

    private CameraImagePicker cameraPicker;

    private CameraVideoPicker cameraVideoPicker;

    private ImagePicker imagePicker;

    private VideoPicker videoPicker;

    private String pickerPath;

    private OnCameraResult onCameraResult;

    public static final int PERMISSION_CODE = 11;

    public CameraUtils(Activity activity, OnCameraResult onCameraResult) {
        this.activity = activity;
        this.onCameraResult = onCameraResult;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                alertPermissionRationale();
            } else {
                requestPermission();
            }
        } else {
            alertCameraGallery();
        }
    }

    public void openCameraGallery() {
        checkPermission();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_CODE);
    }

    private void takePicture() {
        cameraPicker = new CameraImagePicker(activity);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        cameraPicker.setImagePickerCallback(this);
        pickerPath = cameraPicker.pickImage();
    }


    private void takeVideo() {
        Bundle extras = new Bundle();
        // For capturing Low quality videos; Default is 1: HIGH
        //extras.putInt(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        // Set the duration of the video
        extras.putInt(MediaStore.EXTRA_DURATION_LIMIT, 60);
        cameraVideoPicker = new CameraVideoPicker(activity);
        cameraVideoPicker.shouldGenerateMetadata(true);
        cameraVideoPicker.shouldGeneratePreviewImages(true);
        cameraVideoPicker.setVideoPickerCallback(this);
        cameraVideoPicker.setExtras(extras);
        pickerPath = cameraVideoPicker.pickVideo();
    }

    private void pickImageSingle() {
        imagePicker = new ImagePicker(activity);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.pickImage();
    }


    private void pickVideoSingle() {
        videoPicker = new VideoPicker(activity);
        videoPicker.shouldGenerateMetadata(true);
        videoPicker.setVideoPickerCallback(this);
        videoPicker.pickVideo();
    }

    @Override
    public void onImagesChosen(List<ChosenImage> list) {
        if (onCameraResult != null) {
            onCameraResult.onSuccess(list);
        }
    }

    @Override
    public void onError(String s) {
        if (onCameraResult != null) {
            onCameraResult.onError(s);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(activity);
                    imagePicker.setImagePickerCallback(this);
                }
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(activity);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                }
                cameraPicker.submit(data);
            }else if (requestCode == Picker.PICK_VIDEO_DEVICE) {
                if (videoPicker == null) {
                    videoPicker = new VideoPicker(activity);
                    videoPicker.setVideoPickerCallback(this);
                }
                videoPicker.submit(data);
            } else if (requestCode == Picker.PICK_VIDEO_CAMERA) {
                if (cameraVideoPicker == null) {
                    cameraVideoPicker = new CameraVideoPicker(activity);
                    cameraVideoPicker.setVideoPickerCallback(this);
                    cameraVideoPicker.reinitialize(pickerPath);
                }
                cameraVideoPicker.submit(data);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 11:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        alertCameraGallery();
                    } else {
                        boolean showRationale_cam = activity.shouldShowRequestPermissionRationale(permissions[0]);
                        boolean showRationale_storage = activity.shouldShowRequestPermissionRationale(permissions[1]);
                        if (!showRationale_cam && !showRationale_storage) {
                            alertPermissionFromSetting();
                        }
                    }
                    break;
                }
        }
    }

    private void alertPermissionFromSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);
        builder.setMessage(R.string.msg_camera_storage_setting);
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.openApplicationSettings(activity);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void alertPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);
        builder.setMessage(R.string.msg_permission_rationale);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void alertCameraGallery() {
        CharSequence colors[] = new CharSequence[]{activity.getString(R.string.take_photo),
                activity.getString(R.string.choose_from_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);
        builder.setTitle(R.string.choose_image);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                if (which == 0) {
                    takePicture();
                } else {
                    pickImageSingle();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void alertVideoSelcetion() {
        CharSequence colors[] = new CharSequence[]{activity.getString(R.string.take_video),
                activity.getString(R.string.choose_from_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);
        builder.setTitle(R.string.choose_image);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                if (which == 0) {
                    takeVideo();
                } else {
                    pickVideoSingle();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onVideosChosen(List<ChosenVideo> list) {
        if(onCameraResult!=null){
            onCameraResult.onVideoSuccess(list);
        }
    }

    public interface OnCameraResult {
        void onSuccess(List<ChosenImage> images);

        void onVideoSuccess(List<ChosenVideo> videos);

        void onError(String error);
    }
}
