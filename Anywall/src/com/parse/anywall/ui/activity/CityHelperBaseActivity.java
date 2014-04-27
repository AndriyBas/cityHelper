package com.parse.anywall.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.parse.anywall.R;
import com.parse.anywall.ui.fragment.SlidingMenuRightFragment;

/**
 * @author bamboo
 * @since 4/21/14 1:09 AM
 */
public class CityHelperBaseActivity extends SlidingFragmentActivity {

    protected SlidingMenuRightFragment mMenuRightFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlidingMenu sm = getSlidingMenu();


        FrameLayout frame_right = new FrameLayout(this);
        frame_right.setId(R.id.sliding_menu_right);

        // left mRightMenu
        setBehindContentView(frame_right);

        // right mRightMenu
//        sm.setSecondaryMenu(frame_right);

        // so that Sliding mRightMenu shows 2 frames : left and right
        sm.setMode(SlidingMenu.RIGHT);

        // customizing appearance of SlidingMenu
        sm.setSecondaryShadowDrawable(R.drawable.sliding_menu_shadow_right);

        sm.setShadowWidthRes(R.dimen.sliding_menu_shadow_width);
        sm.setShadowDrawable(R.drawable.sliding_menu_shadow_right);
        sm.setBehindOffsetRes(R.dimen.sliding_menu_offset);
        // some Magic Constants
        // TODO need to set width properly
        sm.setBehindWidth(600);
        sm.setFadeDegree(0.35f);

//        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);


        // SlidingMenu saves its state
        if (savedInstanceState == null) {

            mMenuRightFragment = new SlidingMenuRightFragment();


            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.sliding_menu_right, mMenuRightFragment)
                    .commit();

        } else {

            mMenuRightFragment = (SlidingMenuRightFragment)
                    this.getSupportFragmentManager().findFragmentById(R.id.sliding_menu_right);
        }

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                SlidingMenuRightFragment.doOnMenuClosed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
          /*  case android.R.id.home:
                getSlidingMenu().toggle();
                return true;*/

        /*    case R.id.sliding_menu_right_courses_open:
                if (getSlidingMenu().isMenuShowing()) {
                    toggle();
                }
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }
}