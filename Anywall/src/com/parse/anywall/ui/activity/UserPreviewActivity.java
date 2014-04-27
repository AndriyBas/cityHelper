package com.parse.anywall.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseFile;
import com.parse.anywall.Logger;
import com.parse.anywall.R;
import com.squareup.picasso.Picasso;

public class UserPreviewActivity extends Activity {

    ImageView photo;
    TextView name;
    TextView email;
    TextView rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preview);

        photo = (ImageView) findViewById(R.id.avatarPreview);
        name = (TextView) findViewById(R.id.pref_name_value);
        email = (TextView) findViewById(R.id.prev_email_value);
        rating = (TextView) findViewById(R.id.prev_rate_value);
        Intent i = getIntent();
        name.setText(i.getStringExtra("name"));
        email.setText(i.getStringExtra("email"));
        rating.setText(i.getStringExtra("rating"));

        ParseFile f = RateActivity.photo;

        if (f != null) {
            Logger.e(f.getUrl());
            Picasso.with(this)
                    .load(Uri.parse(f.getUrl()))
                    .resize(600, 600)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher)
                    .into(photo);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
