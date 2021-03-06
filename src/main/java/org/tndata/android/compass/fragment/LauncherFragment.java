package org.tndata.android.compass.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.tndata.android.compass.R;

public class LauncherFragment extends Fragment {
    private LauncherFragmentListener mCallback;
    private ProgressBar mProgressBar;
    private Button mSignUpButton;
    private Button mLoginButton;
    private Button mTourButton;

    public interface LauncherFragmentListener {
        public void signUp();

        public void logIn();

        public void tour();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_launcher, container, false);

        mProgressBar = (ProgressBar) v
                .findViewById(R.id.launcher_load_progress);
        mSignUpButton = (Button) v.findViewById(R.id.launcher_sign_up_button);
        mLoginButton = (Button) v.findViewById(R.id.launcher_login_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.signUp();
            }
        });
        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.logIn();
            }
        });
        mTourButton = (Button) v.findViewById(R.id.launcher_tour_button);
        mTourButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallback.tour();
            }
        });
        return v;
    }

    public void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSignUpButton.setVisibility(View.GONE);
            mLoginButton.setVisibility(View.GONE);
            mTourButton.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSignUpButton.setVisibility(View.VISIBLE);
            mLoginButton.setVisibility(View.VISIBLE);
            mTourButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity); // This makes sure that the container activity
        // has implemented the callback interface. If not, it throws an
        // exception
        try {
            mCallback = (LauncherFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LauncherFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
