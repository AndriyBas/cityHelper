package com.parse.anywall.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.parse.anywall.ui.fragment.IssueFragment;

public class IssueActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(getLayoutResId());

        if (fragment == null) {
            fragment = new IssueFragment();
            fm.beginTransaction()
                    .replace(getLayoutResId(), fragment)
                    .commit();
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected int getLayoutResId() {
        return android.R.id.content;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
