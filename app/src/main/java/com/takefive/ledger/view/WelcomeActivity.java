package com.takefive.ledger.view;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.fb.BusinessFbLoginResult;
import com.takefive.ledger.presenter.WelcomePresenter;
import com.takefive.ledger.view.database.SessionStore;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity implements IWelcomeView {

    @Bind(R.id.progress_view)
    CircularProgressView progressView;

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

        SessionStore.initialize();

        // Prepare for animations
        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        Helpers.setMargins(mNameTag, screenSize.y / 3, null, null, null);

        // show title, then login.
        animShowTitleName(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(View.VISIBLE);
                presenter.ledgerLogin();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    void animShowTitleName(Animator.AnimatorListener listener) {
        mNameTag.animate(
        ).translationYBy(screenSize.y / 5 - screenSize.y / 3
        ).alpha(1
        ).setDuration(1000
        ).setListener(listener
        ).start();
    }

    @Override
    public void onLoginSuccess(String username) {
        Log.d("Username", username);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("username", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }

    public void showAlert(String info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    public void showAlert(int info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
