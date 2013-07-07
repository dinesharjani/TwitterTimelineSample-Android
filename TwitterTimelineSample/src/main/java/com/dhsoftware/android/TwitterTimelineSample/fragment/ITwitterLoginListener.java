package com.dhsoftware.android.TwitterTimelineSample.fragment;

/**
 * {@link android.support.v4.app.Fragment Fragment}s can be a bit of a nasty business. In this sample code,
 * we work with a single {@link android.support.v4.app.FragmentActivity Activity} and a single {@link android.support.v4.app.Fragment Fragment}
 * for most of the time. However, when the user logs out or the first time this sample app is run, a second {@link android.support.v4.app.Fragment Fragment}
 * is up, the {@link TwitterLoginFragment}. In the beginning of this sample code, I relied heavily on the first {@link android.support.v4.app.Fragment Fragment}'s
 * {@link android.support.v4.app.Fragment#onResume() onResume()} method to detect when we'd successfully logged in. Then I realized
 * this wasn't a reliable option, so I switched back to the classic callback/Interface/Delegate problem-solver.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/3/13
 */
public interface ITwitterLoginListener {
    /**
     * Called when the {@link com.dhsoftware.android.TwitterTimelineSample.model.TwitterHelper TwitterHelper} class has enough information to provide an {@link twitter4j.auth.AccessToken AccessToken}.
     */
    public void onLoginSuccessful();

}
