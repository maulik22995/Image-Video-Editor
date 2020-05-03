package com.imagevideoeditor.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.imagevideoeditor.BuildConfig;


public class Utils {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int LOCATION_CODE = 10;
    public static final int LOCATION_PERMISSION_CODE = 22;
    public static final int LOCATION_SETTING_REQUEST_CODE = 124;
    public static final int OPEN_SETTINGS_FOR_PERMISSION = 125;



    public static DimensionData getScaledDimension(DimensionData imgSize, DimensionData
            boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        double new_width = original_width;
        double new_height = original_height;

        Log.d("video_width >> ", original_width + " video_height >> " + original_height);
        Log.d("display_width >> ", bound_width + " display_height >> " + bound_height);

        /*if (original_width > original_height) {

        } else {

        }*/


        //scale width to fit
        new_width = bound_width;
        //scale height to maintain aspect ratio
        new_height = (new_width * original_height) / original_width;

//        new_height = ((double)original_width / (double) bound_width) * bound_height;


        // first check if we need to scale width
      /*  if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }*/
        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }


        return new DimensionData((int) new_width, (int) new_height);
    }

    public static void openApplicationSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 0);
    }

}
