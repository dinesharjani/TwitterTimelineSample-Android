package com.dhsoftware.android.twittertimelinesample.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.dhsoftware.android.twittertimelinesample.fragments.TimelineFragment;


/**
 * Simple class: its only job is to make the {@link TimelineFragment} come on screen.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/3/13
 */
public class TimelineActivity extends FragmentActivity {

    private TimelineFragment mTimelineFragment;
    private static final String __TIMELINE_FRAGMENT_TAG__ = TimelineFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // when an Android device rotates, it destroys the Activity but tries to keep the Fragment
        // alive so if we "make" a new Fragment every time the Activity is destroyed, the user will
        // be playing with a 'zombie' Fragment. Simple way of doing this is if Android has
        // saved us some state by itself; if it has, the Fragment is already there for us.
        // If not, standard issue of making a new one and adding it to the Activity.
        if (savedInstanceState != null) {
            mTimelineFragment = (TimelineFragment) getSupportFragmentManager().findFragmentByTag(__TIMELINE_FRAGMENT_TAG__);
        } else {
            mTimelineFragment = new TimelineFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, mTimelineFragment, __TIMELINE_FRAGMENT_TAG__).commit();
        }

    }

    
}
