package com.parse.anywall.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import com.parse.anywall.Const;

import java.io.File;

public class ImageProcessor {

    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, Const.FROM_CAMERA);
    }

    public static void openGallery(Activity activity) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, Const.FROM_GALLERY);
    }

    public static File getTempFile(Activity activity) {
        String state = Environment.getExternalStorageState();
        File mFileTemp;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), Const.TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(activity.getFilesDir(), Const.TEMP_PHOTO_FILE_NAME);
        }
        return mFileTemp;
    }
}
