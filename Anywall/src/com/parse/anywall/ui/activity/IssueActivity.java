package com.parse.anywall.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.parse.anywall.ui.fragment.IssueFragment;

/**
 * Created by badgateway on 26.04.14.
 */
public class IssueActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(getLayoutResId());


        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(getLayoutResId());

        if (fragment == null) {
            fragment = IssueFragment.newInstance(
                    (com.parse.anywall.model.Issue) getIntent()
                            .getSerializableExtra(IssueFragment.KEY_ISSUE)
            );
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
