package com.parse.anywall.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.parse.ParseUser;
import com.parse.anywall.Const;
import com.parse.anywall.Logger;
import com.parse.anywall.R;
import com.parse.anywall.model.ImageProcessor;


public class UserProfileActivity extends Activity implements View.OnClickListener {

    private LinearLayout setAvatar;
    private ImageView avatar;
    private Bitmap bitmapAvatar;
    private LinearLayout name;
    private TextView nameValue;
    private LinearLayout email;
    private TextView emailValue;
    private Button chngPass;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        activity = this;
        initPrefs();
        setupPrefs();
    }

    private void initPrefs() {
        setAvatar = (LinearLayout) findViewById(R.id.prefSetAvatar);
        avatar = (ImageView) findViewById(R.id.avatar);
        name = (LinearLayout) findViewById(R.id.prefName);
        nameValue = (TextView) findViewById(R.id.pref_name_value);
        email = (LinearLayout) findViewById(R.id.prefEmail);
        emailValue = (TextView) findViewById(R.id.pref_email_value);
        chngPass = (Button) findViewById(R.id.chngPass);
    }


    private void setupPrefs() {
        setAvatar.setOnClickListener(this);
        name.setOnClickListener(this);
        email.setOnClickListener(this);
        chngPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prefSetAvatar: {
                showImagePickerDialog(activity, false);
                break;
            }
            case R.id.prefName: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.pref_name));

                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() > 0) {
                            nameValue.setText(input.getText().toString());
                            ParseUser.getCurrentUser().setUsername(input.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            }
            case R.id.prefEmail: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.pref_email));

                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() > 0) {
                            emailValue.setText(input.getText().toString());
                            ParseUser.getCurrentUser().setEmail(input.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            }
            case R.id.chngPass: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Password");

                final EditText input = new EditText(this);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() > 0) {
                            ParseUser.getCurrentUser().setPassword(input.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            }
        }
    }

    private void showImagePickerDialog(final Activity act, final boolean isClose) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(act);
        myAlertDialog.setTitle(act.getString(R.string.dialog_picture_title));
        myAlertDialog.setMessage(act.getString(R.string.dialog_picture_message));

        myAlertDialog.setPositiveButton(act.getString(R.string.dialog_picture_gallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ImageProcessor.openGallery(act);
                    }
                });
        myAlertDialog.setNegativeButton(act.getString(R.string.dialog_picture_camera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ImageProcessor.openCamera(act);
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tags_choose_save:
                saveClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveClicked() {
        ParseUser.getCurrentUser().saveEventually();
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.FROM_CAMERA) {
                Logger.d("requestCode == FROM_CAMERA");
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap bitMap = (Bitmap) extras.get("data");
                    avatar.setImageBitmap(bitMap);
                } else {
                    Logger.d("from camera data == null");
                }
            }
            if (requestCode == Const.FROM_GALLERY) {
                Logger.d("requestCode == FROM_GALLERY");
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    avatar.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Logger.e(getClass().getSimpleName() + ":  Error while FROM_GALLERY");
                }
            }

        }
    }
}
