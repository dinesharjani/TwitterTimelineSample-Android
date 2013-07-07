package com.dhsoftware.android.TwitterTimelineSample.model;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * This Interface is not used, but it was designed to make the process of giving Twitter the user's PIN code
 * back to confirm they've rightfully authorized us asynchronous. In the end, I did not fully deem it
 * necessary to write the code this way, but the Interface itself doesn't have to die; I might come
 * back later to improve the code.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/5/13
 */
public interface IAccessTokenRequestCallback {

    public void onAccessTokenDenied();
    public void onAccessTokenObtained(AccessToken accessToken);

}
