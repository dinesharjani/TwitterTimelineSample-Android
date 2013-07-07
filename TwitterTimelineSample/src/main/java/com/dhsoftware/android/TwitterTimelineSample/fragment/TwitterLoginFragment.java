package com.dhsoftware.android.TwitterTimelineSample.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dhsoftware.android.TwitterTimelineSample.R;
import com.dhsoftware.android.TwitterTimelineSample.model.IRequestTokenRequestCallback;
import com.dhsoftware.android.TwitterTimelineSample.model.TwitterHelper;
import twitter4j.auth.RequestToken;

/**
 * This {@link android.support.v4.app.Fragment Fragment} takes care of all login proceedings.
 * When done, if there's a {@link ITwitterLoginListener} set, it will inform it of a
 * successful login. Once this {@link android.support.v4.app.Fragment Fragment} makes it to the screen however,
 * the user won't be able to get out of it until login is successful.
 * <br></br>
 * <br></br>
 * User: Dinesh Harjani (email: goldrunner18725@gmail.com) (github: the7thgoldrunner) (Twitter: @dinesharjani)
 * <br></br>
 * Date: 7/3/13
 */
public class TwitterLoginFragment extends DialogFragment {

    /**
     * A way of identifying whether this {@link android.support.v4.app.Fragment Fragment} is present or not in
     * the {@link android.support.v4.app.Fragment Fragment} back stack.
     */
    public static final String __LOGIN_FRAGMENT_TAG__ = TwitterLoginFragment.class.getSimpleName();

    private RequestToken mRequestToken;
    private ITwitterLoginListener mLoginListener;

    private ProgressBar mProgressBar;
    private TextView mDescriptionTextView;
    private EditText mPinEditText;
    private Button mOkButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewgroup, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.twitterlogin_fragment, viewgroup);
        initUIConfig((ViewGroup)v);
        return v;
    }

    private void initUIConfig(ViewGroup viewGroup) {
        getDialog().setTitle(R.string.com_dhsoftware_android_twitterTimelineSample_twitterLoginFragment_title);

        mProgressBar = (ProgressBar) viewGroup.findViewById(R.id.com_dhsoftware_android_twitterTimelineSample_twitterLoginFragment_progressBar);
        mDescriptionTextView = (TextView) viewGroup.findViewById(R.id.com_dhsoftware_android_twitterTimelineSample_twitterLoginFragment_descriptionTextView);
        mPinEditText = (EditText) viewGroup.findViewById(R.id.com_dhsoftware_android_twitterTimelineSample_twitterLoginFragment_pinEditText);
        mOkButton = (Button) viewGroup.findViewById(R.id.com_dhsoftware_android_twitterTimelineSample_twitterLoginFragment_okButton);

        // user can't walk away from logging in
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);

        // 'OK' button used to process PIN
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = mPinEditText.getText().toString();
                processPin(pin);
            }
        });

        // until we get a Request Token, we do not show the description text, the PIN code EditText
        // nor the 'OK' button.
        TwitterHelper.getRequestToken(getActivity(), new IRequestTokenRequestCallback() {
            @Override
            public void onRequestCompleted(RequestToken requestToken) {

                mRequestToken = requestToken;

                String requestURL = requestToken.getAuthorizationURL();
                String descriptionString = String.format(getResources().getString(R.string.com_dhsoftware_android_twitterTimelineSample_twitterLoginFragment_description), requestURL);
                mDescriptionTextView.setText(descriptionString);

                mProgressBar.setVisibility(View.GONE);
                mDescriptionTextView.setVisibility(View.VISIBLE);
                mPinEditText.setVisibility(View.VISIBLE);
                mOkButton.setVisibility(View.VISIBLE);
            }
        });

    }

    public void setLoginListener(ITwitterLoginListener loginListener) {
        mLoginListener = loginListener;
    }

    private void processPin(String pin) {
        TwitterHelper.processOAuthVerifier(this.getActivity(), mRequestToken, pin);

        // if login successful
        if (TwitterHelper.isLoggedIn(getActivity())) {

            dismiss();
            if (mLoginListener != null) {
                mLoginListener.onLoginSuccessful();
            }
        }
        else {
            // message user
            Toast.makeText(getActivity(), getString(R.string.com_dhsoftware_android_twitterTimelineSample_twitterLoginFragment_error), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A simple helper method so the logic for making this {@link DialogFragment} come on screen is kept in one single place.
     */
    public static void showLoginFragment(FragmentActivity activity, ITwitterLoginListener listener) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        // pre-detect if there was a previous Dialog and pop it off the Fragment Stack
        Fragment previousDialog = activity.getSupportFragmentManager().findFragmentByTag(__LOGIN_FRAGMENT_TAG__);
        if (previousDialog != null) {
            ft.remove(previousDialog);
        }
        ft.addToBackStack(null);

        TwitterLoginFragment newDialog = new TwitterLoginFragment();
        newDialog.setLoginListener(listener);
        // this line is specific for FragmentDialogs and it adds the Fragment to the stack
        // plus takes care of committing the FragmentTransaction
        newDialog.show(ft, __LOGIN_FRAGMENT_TAG__);
    }

}
