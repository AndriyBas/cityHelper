package com.parse.anywall.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import com.parse.anywall.Const;
import com.parse.anywall.Logger;
import com.parse.anywall.R;

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


    public static void openCamera(Fragment fragment) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fragment.startActivityForResult(intent, Const.FROM_CAMERA);
    }

    public static void openGallery(Fragment fragment) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        fragment.startActivityForResult(photoPickerIntent, Const.FROM_GALLERY);
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

    public static void showImagePickerDialog(final Activity act, final boolean isClose) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(act);
        myAlertDialog.setTitle(act.getString(R.string.dialog_picture_title));
        myAlertDialog.setMessage(act.getString(R.string.dialog_picture_message));

        myAlertDialog.setPositiveButton(act.getString(R.string.dialog_picture_gallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ImageProcessor.openGallery(act);
                    }
                }
        );
        myAlertDialog.setNegativeButton(act.getString(R.string.dialog_picture_camera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ImageProcessor.openCamera(act);
                    }
                }
        );
        myAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Logger.d("myAlertDialog onCancel");
                if (isClose) {
                    act.finish();
                }
            }
        });
        myAlertDialog.show();
    }

    public static void showImagePickerDialog(final Fragment act, final boolean isClose) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(act.getActivity());
        myAlertDialog.setTitle(act.getString(R.string.dialog_picture_title));
        myAlertDialog.setMessage(act.getString(R.string.dialog_picture_message));

        myAlertDialog.setPositiveButton(act.getString(R.string.dialog_picture_gallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ImageProcessor.openGallery(act);
                    }
                }
        );
        myAlertDialog.setNegativeButton(act.getString(R.string.dialog_picture_camera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ImageProcessor.openCamera(act);
                    }
                }
        );
        myAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Logger.d("myAlertDialog onCancel");
                if (isClose) {
                    act.getActivity().finish();
                }
            }
        });
        myAlertDialog.show();
    }
}
