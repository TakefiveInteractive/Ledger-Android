package com.takefive.ledger.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.fb.BusinessFbLoginResult;
import com.takefive.ledger.presenter.WelcomePresenter;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zyu19.libs.action.chain.config.NiceConsumer;

public class WelcomeActivity extends AppCompatActivity implements IWelcomeView {

    @Bind(R.id.backgroundImage)
    ImageView mBgImg;

    @Bind(R.id.progress_view)
    CircularProgressView progressView;

    @Bind(R.id.login)
    BootstrapButton mLogin;

    @Bind(R.id.nameTag)
    TextView mNameTag;

    CallbackManager mFBCallbackManager;

    @Inject
    WelcomePresenter presenter;

    Point screenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        ((MyApplication) getApplication()).inject(this);
        presenter.attachView(this);

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        mFBCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFBCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                progressView.setVisibility(View.VISIBLE);
                BusinessFbLoginResult businessFbLoginResult = new BusinessFbLoginResult();
                businessFbLoginResult.setToken(loginResult.getAccessToken());
                presenter.ledgerLogin(businessFbLoginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                showAlert(R.string.error_contact_facebook);
            }
        });

        // Prepare for animations
        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        Helpers.setMargins(mNameTag, screenSize.y / 3, null, null, null);
        mLogin.setAlpha(0);

        if(AccessToken.getCurrentAccessToken() != null) {
            animShowTitleName();
            progressView.setVisibility(View.VISIBLE);
            presenter.ledgerLogin(new BusinessFbLoginResult(AccessToken.getCurrentAccessToken()));
        } else {
            animShowButton();
            animShowTitleName();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    void animShowButton() {
        mLogin.animate(
        ).alpha(1
        ).setDuration(1000
        ).start();
    }

    void animShowTitleName() {
        mNameTag.animate(
        ).translationYBy(screenSize.y / 5 - screenSize.y / 3
        ).alpha(1
        ).setDuration(1000
        ).start();
    }

    @OnClick(R.id.login)
    public void login() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    @Override
    public void onLoginSuccess(String username) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("username", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }

    public void showAlert(String info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
        afterLoginFailure();
    }

    public void showAlert(int info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
        afterLoginFailure();
    }

    public void afterLoginFailure() {
        // We need to check whether the spinner is still spinning, and whether buttons are still visible
        progressView.setVisibility(View.GONE);
        if(mLogin.getAlpha() < 0.5f || mLogin.getVisibility() != View.VISIBLE)
            animShowButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
