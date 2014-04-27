package com.parse.anywall.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tagmanager.PreviewActivity;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.anywall.Logger;
import com.parse.anywall.R;
import com.parse.anywall.model.UserData;
import com.squareup.picasso.Picasso;

public class RateActivity extends Activity implements AdapterView.OnItemClickListener {
    ParseQueryAdapter<UserData> mUserDataAdapter;
    ListView mUserDataListView;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rate);

        mUserDataListView = (ListView) findViewById(R.id.rateLv);
        activity = this;

        ParseQueryAdapter.QueryFactory<UserData> factory =
                new ParseQueryAdapter.QueryFactory<UserData>() {
                    public ParseQuery<UserData> create() {

                        ParseQuery<UserData> query = ParseQuery.getQuery(UserData.class);

                        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                        query.include("userOK");
                        query.orderByDescending("rating");

                        query.setLimit(100);
                        return query;
                    }
                };

        mUserDataAdapter = new ParseQueryAdapter<UserData>(this, factory) {
            @Override
            public View getItemView(UserData d, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.rate_item, null);
                }
                TextView tt = (TextView) view.findViewById(R.id.tvUserName);
                TextView tt1 = (TextView) view.findViewById(R.id.tvRate);
                ImageView iv = (ImageView) view.findViewById(R.id.ivUserPhoto);

                tt.setText(d.getUser().getUsername());
                tt1.setText("" + d.getRating());
                ParseFile f = d.getPhoto();

                if (f != null) {
                    Logger.e(f.getUrl());
                    Picasso.with(activity)
                            .load(Uri.parse(f.getUrl()))
                            .resize(300, 300)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher)
                            .into(iv);
                }
                return view;
            }
        };

        mUserDataAdapter.setAutoload(false);

        mUserDataAdapter.setPaginationEnabled(false);
        mUserDataListView.setOnItemClickListener(this);
        mUserDataListView.setAdapter(mUserDataAdapter);
        Logger.d("passed rate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserDataAdapter.loadObjects();
    }

    public static ParseFile photo;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(activity, UserPreviewActivity.class);
        intent.putExtra("name", mUserDataAdapter.getItem(position).getUser().getUsername());
        intent.putExtra("email", mUserDataAdapter.getItem(position).getUser().getEmail());
        intent.putExtra("rating", ""+mUserDataAdapter.getItem(position).getRating());
        photo = mUserDataAdapter.getItem(position).getPhoto();
        startActivity(intent);
    }
}
