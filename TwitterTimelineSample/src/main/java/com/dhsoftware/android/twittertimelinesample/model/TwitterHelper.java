package com.dhsoftware.android.twittertimelinesample.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


import com.dhsoftware.android.twittertimelinesample.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Although the Twitter4j library we're using handles most of the hard work of interfacing with Twitter itself,
 * there are some parts that require a few lines of code here and there. To make the sample code itself
 * more readable, and also in aid of good Software Engineering, I encapsulated all of that work inside of
 * this static class. It's a stateless class for the outside user, although internally it needs to hold
 * on to a variable to handle logouts, since Twitter4j doesn't truly have a native concept of 'logout'
 * because it's based in OAuth 2.0 tokens.
 * <br></br>
 * One simple note: We're using Android's {@link android.content.SharedPreferences} to save our OAuth Token information. This is NOT
 * recommended for a real application, since SharedPreferences are really just an XML file handled through
 * an OS API for us. That XML file is safe from peeking as long as the device the code is running on isn't rooted,
 * so to use {@link android.content.SharedPreferences} some sort of encryption would be recommended. There are many solutions for this
 * problem, choose the one that best fits your code or software.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/3/13
 */
public final class TwitterHelper {

    private static final String __TWITTER_TOKEN__ = "token";
    private static final String __TWITTER_TOKEN_SECRET__ = "tokenSecret";
    private static boolean mConsumerKeysSet = false;

    /**
     * Simple getter method taking care of accessing {@link android.content.SharedPreferences} for recovering our current
     * OAuth Token.
     */
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString(__TWITTER_TOKEN__, "");
        if (token.length() > 0) {
            return token;
        }
        else {
            return null;
        }
    }

    /**
     * Just as the {@link com.dhsoftware.android.twittertimelinesample.model.TwitterHelper#getToken(android.content.Context context) getToken()} method, this one interfaces
     * with {@link android.content.SharedPreferences} and returns our current OAuth Token's TokenSecret value.
     */
    public static String getTokenSecret(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tokenSecret = sharedPreferences.getString(__TWITTER_TOKEN_SECRET__, "");
        if (tokenSecret.length() > 0) {
            return tokenSecret;
        }
        else {
            return null;
        }
    }

    /**
     * This method only exists to make working with {@link android.content.SharedPreferences} easier in this class: it sets
     * and saves the information necessary to produce an OAuth Token when required.
     */
    private static void setTokenAndtokenSecret(Context context, String token, String tokenSecret) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(__TWITTER_TOKEN__, token);
        sharedPreferencesEditor.putString(__TWITTER_TOKEN_SECRET__, tokenSecret);
        sharedPreferencesEditor.commit();
    }

    /**
     * Helper method for this sample application to know whether we have an OAuth Token or not.
     * (i.e. are we logged in?)
     */
    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        String tokenSecret = getTokenSecret(context);
        return ((token != null) && (tokenSecret != null) && (token.length() > 0) && (tokenSecret.length()) > 0);
    }

    /**
     * Helper method for erasing the information we have to produce a new OAuthToken for interacting
     * with Twitter for the current user. To prevent the library from producing an exception, we
     * must also take away the current Token from it, because with OAuth 2.0 there's no concept of
     * "connection"; apps receive a Token and they can use it as long as it's valid.
     */
    public static void logout(Context context) {
        setTokenAndtokenSecret(context, "", "");
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.shutdown();
        if (mConsumerKeysSet) {
            twitter.setOAuthAccessToken(null);
        }
    }

    /**
     * With a valid Token and TokenSecret information, we can produce an OAuth Access Token
     * whenever we need to.
     */
    public static AccessToken makeOAuthAccessToken(Context context) {
        String token = getToken(context);
        String tokenSecret = getTokenSecret(context);
        return new AccessToken(token, tokenSecret);
    }

    /**
     * If we can't make an OAuth Access Token (i.e. not logged in), we need to tell the user to
     * authorize us so we can make one. That's what the RequestToken is for, it provides us
     * with an URL we can give the user so they can tell Twitter to assign us a Token.
     */
    private static RequestToken makeRequestToken(Context context) {
        String twitterConsumerKey = context.getString(R.string.com_dhsoftware_android_twitterTimelineSample_twitterConsumerKey);
        String twitterConsumerSecret = context.getString(R.string.com_dhsoftware_android_twitterTimelineSample_twitterConsumerSecret);
        RequestToken requestToken;

        try {

            Twitter twitter = TwitterFactory.getSingleton();
            if (!mConsumerKeysSet) {
                twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
                mConsumerKeysSet = true;
            }
            requestToken = twitter.getOAuthRequestToken();

        } catch (Exception exception) {
            exception.printStackTrace();
            requestToken = null;
        }

        return requestToken;
    }

    /**
     * The code for producing a new RequestToken which is represented by the {@link com.dhsoftware.android.twittertimelinesample.model.TwitterHelper#makeRequestToken(android.content.Context) requestToken()} method
     * is actually slow, since it interfaces with Twitter itself. To solve this, and make the UI much more responsible, we actually run that code inside
     * an {@link android.os.AsyncTask} without the knowledge of the user (programmer) and we give them the Token through a callback interface defined by {@link IRequestTokenRequestCallback}.
     */
    public static void getRequestToken(final Context context, final IRequestTokenRequestCallback callback) {

        AsyncTask<Void, Void, Void> backgroundThread = new AsyncTask<Void, Void, Void>() {

            private RequestToken mRequestToken;

            @Override
            protected Void doInBackground(Void... voids) {
                mRequestToken = makeRequestToken(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void object) {
                if (callback != null) {
                    callback.onRequestCompleted(mRequestToken);
                }
            }
        };
        backgroundThread.execute();
    }

    /**
     * This is the second and last step to get a Twitter OAuth Authorization Token: when the user goes to the URL provided by the RequestToken he/she will be
     * provided with a PIN number we need to give Twitter to verify the user has really authorized us. The code in this method also interfaces with the
     * network, so it could make use of a similar strategy as the {@link com.dhsoftware.android.twittertimelinesample.model.TwitterHelper#getRequestToken(android.content.Context, IRequestTokenRequestCallback) getRequestToken()} method,
     * so much that in fact I wrote a specific callback interface for that purpose, {@link IAccessTokenRequestCallback}. However, making this code
     * synchronous did not interfere too much with the User Experience, at least running in the Emulator. Considering it's a sample code, I call this
     * an "informed decision" to not make this small code improvement.
     * <br></br>
     * I will be happy, however, if any of you decide to make this small change and send me a Pull Request :)
     * @param context a {@link android.content.Context} variable to access {@link android.content.SharedPreferences}, such as an {@link android.app.Activity Activity}.
     * @param requestToken the {@link twitter4j.auth.RequestToken} the User used to get a PIN number from Twitter itself.
     * @param pinCode the PIN number provided by Twitter to the user.
     */
    public static void processOAuthVerifier(Context context, RequestToken requestToken, String pinCode) {
        try {
            AccessToken accessToken;
            if (pinCode.length() > 0) {
                accessToken = TwitterFactory.getSingleton().getOAuthAccessToken(requestToken, pinCode);
            }
            else {
                accessToken = TwitterFactory.getSingleton().getOAuthAccessToken();
            }

            setTokenAndtokenSecret(context, accessToken.getToken(), accessToken.getTokenSecret());

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }



}
