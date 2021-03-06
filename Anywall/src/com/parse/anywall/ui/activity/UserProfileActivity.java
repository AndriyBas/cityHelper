package com.parse.anywall.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.parse.*;
import com.parse.anywall.Const;
import com.parse.anywall.Logger;
import com.parse.anywall.R;
import com.parse.anywall.model.ImageProcessor;
import com.parse.anywall.model.UserData;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


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
    private LinearLayout rate;
    private TextView rateValue;

    private UserData uData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        activity = this;
        initPrefs();
        setupPrefs();

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initPrefs() {
        setAvatar = (LinearLayout) findViewById(R.id.prefSetAvatar);
        avatar = (ImageView) findViewById(R.id.avatar);
        name = (LinearLayout) findViewById(R.id.prefName);
        nameValue = (TextView) findViewById(R.id.pref_name_value);
        email = (LinearLayout) findViewById(R.id.prefEmail);
        emailValue = (TextView) findViewById(R.id.pref_email_value);
        chngPass = (Button) findViewById(R.id.chngPass);
        rate = (LinearLayout) findViewById(R.id.prefRate);
        rateValue = (TextView) findViewById(R.id.pref_rate_value);

        if (ParseUser.getCurrentUser() != null) {
            nameValue.setText(ParseUser.getCurrentUser().getUsername());
            emailValue.setText(ParseUser.getCurrentUser().getEmail());
        }

        ParseQuery<UserData> query = ParseQuery.getQuery(UserData.class);

        query.whereEqualTo("userOK", ParseUser.getCurrentUser());

        query.getFirstInBackground(new GetCallback<UserData>() {
            @Override
            public void done(UserData userData, ParseException e) {
                if (e == null) {
                    uData = userData;
                    ParseFile f = uData.getPhoto();

                    if (f != null) {
                        Logger.e(f.getUrl());
                        Picasso.with(activity)
                                .load(Uri.parse(f.getUrl()))
                                .resize(300, 300)
                                .centerCrop()
                                .placeholder(R.drawable.stub)
                                .into(avatar);
                    }
                    rateValue.setText("" + uData.getRating());
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
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
                ImageProcessor.showImagePickerDialog(activity, false);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.menu_tags_choose_save:
                saveClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveClicked() {
//        if (uData==null) uData
        if (bitmapAvatar != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapAvatar.compress(Bitmap.CompressFormat.JPEG, 35, stream);
            byte[] data = stream.toByteArray();
            ParseFile file = new ParseFile(System.currentTimeMillis() + ".jpeg", data);
            uData.setPhoto(file);
        } else {
            Logger.e("need add photo");
        }
        uData.saveInBackground();
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
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    bitmapAvatar = bitmap;
                    avatar.setImageBitmap(bitmap);
                } else {
                    Logger.d("from camera data == null");
                }
            }
            if (requestCode == Const.FROM_GALLERY) {
                Logger.d("requestCode == FROM_GALLERY");
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    bitmapAvatar = bitmap;
                    avatar.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Logger.e(getClass().getSimpleName() + ":  Error while FROM_GALLERY");
                }
            }

        }
    }
}
