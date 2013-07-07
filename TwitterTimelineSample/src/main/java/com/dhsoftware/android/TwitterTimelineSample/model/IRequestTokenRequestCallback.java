package com.dhsoftware.android.TwitterTimelineSample.model;

import twitter4j.auth.RequestToken;

/**
 * When the user logs out or if the user has never logged in with our application,
 * the first thing we need to do is obtain an OAuth Request Token off of Twitter so
 * we can point the user to a specific link so they can effectively authorize our
 * app. This is a lengthy operation, since it involves network access. To make
 * the {@link com.dhsoftware.android.TwitterTimelineSample.fragment.TwitterLoginFragment TwitterLoginFragment}
 * appear much faster, I made this callback Interface so the {@link TwitterHelper} class
 * can notify the {@link com.dhsoftware.android.TwitterTimelineSample.fragment.TwitterLoginFragment TwitterLoginFragment}
 * when it has obtained a new {@link RequestToken}.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/5/13
 */
public interface IRequestTokenRequestCallback {

    public void onRequestCompleted(RequestToken requestToken);

}
