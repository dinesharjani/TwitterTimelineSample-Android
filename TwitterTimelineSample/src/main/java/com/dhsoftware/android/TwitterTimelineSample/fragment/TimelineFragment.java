package com.dhsoftware.android.TwitterTimelineSample.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dhsoftware.android.TwitterTimelineSample.R;
import com.dhsoftware.android.TwitterTimelineSample.adapters.TimelineStatusAdapter;
import com.dhsoftware.android.TwitterTimelineSample.loaders.TwitterTimelineLoader;
import com.dhsoftware.android.TwitterTimelineSample.model.TwitterHelper;

import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Where all of the pieces come together at last. What it basically does is call {@link TwitterLoginFragment} if
 * we're not signed in, show the Twitter Timeline, and allow the user to both refresh the timeline and
 * logout as they wish.
 * <br></br>
 * You'll find it a bit odd that for refreshing I'm not using the same <a href="https://github.com/chrisbanes/Android-PullToRefresh">Pull-to-Refresh library as the last time</a>,
 * the reason being since this is an Android Studio project, I've had to deal with Gradle, and I haven't been able to make it
 * work importing it. As a matter of fact, said library is a part of this sample code. I hope I can get it to fully work
 * soon.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/2/13
 */
public class TimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<ResponseList<Status>> {

    /**
     * The ID for the menu option to refresh the Twitter Timeline.
     */
    private static final int __MENU_REFRESH_OPTION__ = Menu.FIRST;
    /**
     * The ID for the menu option to logout the user.
     */
    private static final int __MENU_LOGOUT_OPTION__ = Menu.FIRST + 1;

    /**
     * A unique ID given to every {@link Loader} so it can be identified later on.
     */
    private static final int __TIMELINE_LOADER_ID__ = 0;

    private boolean mLoginVisible;
    private boolean mRefreshing;
    private ListView mTimelineListView;
    private TimelineStatusAdapter mTimelineAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if we forget this line, we won't have a Menu!
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_fragment, viewGroup, false);
        // UIConfig goes first because
        initUIConfig((ViewGroup)view);
        // dataConfig sets the listView's adapter
        initDataConfig();
        return view;
    }

    private void initUIConfig(ViewGroup viewGroup) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mTimelineListView = (ListView) viewGroup.findViewById(R.id.timelineFragment_timelineListView);
    }

    private void initDataConfig() {
        mLoginVisible = false;
        mRefreshing = false;
        mTimelineAdapter = new TimelineStatusAdapter(this.getActivity());
        mTimelineListView.setAdapter(mTimelineAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * Very simple: if we're not logged in, we make a {@link TwitterLoginFragment} appear to handle that duty.
     * Once it's finished, we automatically refresh the Timeline. If we're logged in, we also automatically
     * refresh the Timeline.
     * Lastly, the <code>mLoginVisible</code> variable takes care of the case when the {@link TwitterLoginFragment} is
     * already visible and the user hasn't yet logged in.
     */
    private void updateUI() {
        if (!TwitterHelper.isLoggedIn(this.getActivity())) {
            if (!mLoginVisible) {
                TwitterLoginFragment.showLoginFragment(this.getActivity(), new ITwitterLoginListener() {
                    @Override
                    public void onLoginSuccessful() {
                        mLoginVisible = false;
                        refreshTimeline();
                    }
                });
                mLoginVisible = true;
            }
        }
        else {
            refreshTimeline();
        }
    }

    /**
     * It's all very simple: if we're logged in, we make the {@link TwitterTimelineLoader} provide us with Tweets.
     * If the {@link TwitterTimelineLoader} hasn't been created yet, we init it, and if it has, we restart it.
     * You cannot "refresh" a Loader through the standard API.
     */
    private void refreshTimeline() {
        if (TwitterHelper.isLoggedIn(this.getActivity())) {
            mRefreshing = true;
            if (mTimelineAdapter.isEmpty()) {
                getActivity().getSupportLoaderManager().initLoader(__TIMELINE_LOADER_ID__, null, this);
            }
            else {
                getActivity().getSupportLoaderManager().restartLoader(__TIMELINE_LOADER_ID__, null, this);
            }
        }

        reloadMenu();
    }

    private void logout() {
        TwitterHelper.logout(getActivity());
        // we won't need this loader anymore
        this.getActivity().getSupportLoaderManager().destroyLoader(__TIMELINE_LOADER_ID__);
        // if we logout, all the data we have is rendered invalid, so we clear it
        mTimelineAdapter.clear();
        // updateUI() will cause the login Dialog to appear
        updateUI();
    }

    /**
     * The Menu is how the User can both refresh the Timeline and Logout, so
     * updating its state is very important, so we have a method to deal with it
     * specifically.
     */
    private void reloadMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().invalidateOptionsMenu();
        }
        else {
            // alternative method from the Android Support Library
            ActivityCompat.invalidateOptionsMenu(getActivity());
        }
    }

    /*
    Loader Callbacks implementation
     */
    public Loader<ResponseList<Status>> onCreateLoader(int loaderId, Bundle args) {
        return new TwitterTimelineLoader(this.getActivity(), TwitterHelper.makeOAuthAccessToken(this.getActivity()));
    }

    @Override
    public void onLoadFinished(Loader<ResponseList<Status>> loader, ResponseList<Status> newStatus) {
        mTimelineAdapter.clear();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (newStatus != null) {
                // new API added at Level 11
                mTimelineAdapter.addAll(newStatus);
            }
        }
        else {
            if (newStatus != null) {
                // classic way of adding elements to the Adapter
                // (i.e. like in the Cro-Magnon Era, as my Maths teacher in High School'd say)
                for (Status status : newStatus) {
                    mTimelineAdapter.add(status);
                }
            }
        }

        mRefreshing = false;
        // the user can refresh again now
        reloadMenu();
    }

    @Override
    public void onLoaderReset(Loader<ResponseList<Status>> loader) {
        // nothing to do
        // what we really want with the Loader reset is to force the Loader
        // to fetch new content and to get a call to onLoadFinished().
    }

    /*
    * Menu handling
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (TwitterHelper.isLoggedIn(this.getActivity())) {
            if (!mRefreshing) {
                menu.add(0, __MENU_REFRESH_OPTION__, 0, R.string.com_dhsoftware_android_twitterTimelineSample_refresh);
            }
            menu.add(0, __MENU_LOGOUT_OPTION__, 0, R.string.com_dhsoftware_android_twitterTimelineSample_logout);
        }
        else {
            menu.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case __MENU_LOGOUT_OPTION__:
                logout();
                return true;
            case __MENU_REFRESH_OPTION__: {
                updateUI();
                return true;
            }
            default:
                break;
        }
        // if it's not one of our options, we let Android handle them
        return super.onOptionsItemSelected(item);
    }

}
