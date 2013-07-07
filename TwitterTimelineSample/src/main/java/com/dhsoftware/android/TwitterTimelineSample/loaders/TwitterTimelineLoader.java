package com.dhsoftware.android.TwitterTimelineSample.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.dhsoftware.android.TwitterTimelineSample.R;
import com.dhsoftware.android.TwitterTimelineSample.model.TwitterHelper;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Loaders. Loaders were this thing first introduced in Honeycomb and which I've been managing to stay away
 * from for quite some time; primarily because we all learnt by ourselves not to load from SQLite Databases
 * on the Main Thread (right?) which were the problem Google supposedly wanted to improve by adding this new
 * API. For me, making these Sample Code projects are also an opportunity to try out new things I've not
 * used in production code, so one of the things I decided to try out here was using a Loader.
 * <br></br>
 * Unfortunately for both of us, there's a reason why I haven't felt the need for them during my career: they
 * can be avoided. Loaders seem mostly designed for Cursors, not for the kind of thing we need displaying
 * Facebook or Twitter Timelines; the Code Architecture I used in my <a href="https://github.com/the7thgoldrunner/FacebookNewsfeedSample-Android/">FacebookNewsfeedSampleCode</a>
 * fits a lot better, because a Loader is supposed to "hold onto" all the data, just as an {@link android.widget.Adapter Adapter},
 * which begs the question: "what is an {@link android.widget.Adapter Adapter} to do, then?" I think some sort
 * of composition would be the solution, but to keep the code simple (and quick, I'm on a deadline),
 * I didn't do anything fancy.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/3/13
 */
public class TwitterTimelineLoader extends AsyncTaskLoader<ResponseList<Status>> {

    private static Twitter mTwitter;
    private static ResponseList<Status> mTimeline;
    private static Paging mPaging;

    public TwitterTimelineLoader(Context context, AccessToken accessToken) {
        super(context);
        init(accessToken);
    }

    /**
     * For our Loader to work, we require a valid {@link AccessToken} plus our own app's Consumer Key and Consumer
     * Secret. Note that this code is not "guarded" against null {@link AccessToken}s.
     * @param accessToken a valid {@link AccessToken}, in this sample code obtained through the {@link TwitterHelper} class.
     */
    private void init(AccessToken accessToken) {

        if (mTwitter == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setOAuthConsumerKey(getContext().getString(R.string.com_dhsoftware_android_twitterTimelineSample_twitterConsumerKey));
            cb.setOAuthConsumerSecret(getContext().getString(R.string.com_dhsoftware_android_twitterTimelineSample_twitterConsumerSecret));
            cb.setOAuthAccessToken(accessToken.getToken());
            cb.setOAuthAccessTokenSecret(accessToken.getTokenSecret());

            TwitterFactory twitterFactory = new TwitterFactory(cb.build());
            mTwitter = twitterFactory.getInstance();
        }
    }

    /**
     * Here's a big issue: Loaders don't have a "refresh" method. The closest thing there is
     * out there is to restart the Loader. To that end, the most obvious and simplest solution
     * was to make all of our state of static type, specially since when a Loader is restarted,
     * it is effectively destroyed and replaced with a new one. To "go around" this behavior,
     * static is the way to go. The downside is that you cannot have more than one Loader in
     * your application. But this is just a sample code.
     */
    @Override protected void onStartLoading() {
        // we always forceLoad, because we always want to check
        // for new content
        // and since our content 'lives' through Loader destruction and creation,
        // we're fine
        forceLoad();
    }

    /**
     * The main idea behind this Loader's work is the following: the first time, we ask
     * Twitter for the latest tweets. After that, we ask Twitter only for any new
     * Tweets that have come around since the last time we ask. Notice how the Timeline
     * can easily be broken because we're not considering "Gaps" in here, but that's for
     * you, this sample code's user, to make.
     */
    @Override
    public ResponseList<Status> loadInBackground() {
        try {
            if (mPaging == null) {
                // initialize timeline
                mTimeline = mTwitter.getHomeTimeline();
            }
            else {
                // update the timeline
                ResponseList<Status> newStatus = mTwitter.getHomeTimeline(mPaging);

                // add new Statuses in reverse order to preserve
                // the Timeline
                for (int i = newStatus.size() - 1; i >= 0; i--) {
                    Status status = newStatus.get(i);
                    mTimeline.add(0, status);
                }
            }

            // update paging variable so we
            // can request Twitter tweets newer than
            // the most recent one we have
            Status firstStatus = mTimeline.get(0);
            mPaging = new Paging(firstStatus.getId());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return mTimeline;
    }

}
