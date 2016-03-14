package com.takefive.ledger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.takefive.ledger.client.LedgerService;
import com.takefive.ledger.database.UserStore;
import com.takefive.ledger.model.Person;
import com.takefive.ledger.task.FbUserInfo;
import com.takefive.ledger.task.FbUserInfoTask;
import com.takefive.ledger.task.LoginTask;
import com.takefive.ledger.task.UpdateUserInfoTask;
import com.takefive.ledger.task.UserInfoUpdatedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.config.ThreadPolicy;

public class WelcomeActivity extends AppCompatActivity {

    @Bind(R.id.backgroundImage)
    ImageView mBgImg;

    @Bind(R.id.login)
    BootstrapButton mLogin;

    @Bind(R.id.nameTag)
    TextView mNameTag;

    CallbackManager mFBCallbackManager;

    @Inject
    UserStore userStore;

    @Inject
    Realm realm;

    @Inject
    LedgerService service;

    @Inject
    Bus bus;

    @Inject
    LoginTask loginTask;

    @Inject
    UpdateUserInfoTask userInfoTask;

    FbUserInfoTask fbUserInfoTask = new FbUserInfoTask();

    @Inject
    ActionChainFactory chainFactory;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).inject(this);

        bus.register(this);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        Helpers.setMargins(mNameTag, screenSize.y / 3, null, null, null);
        Helpers.setMargins(mLogin, null, -getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin), null, null);
        ViewPropertyAnimator[] animators = new ViewPropertyAnimator[]{
                mLogin.animate()
                        .translationYBy(-getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin))
                        .alpha(1)
                        .setDuration(1000),
                mNameTag.animate()
                        .translationYBy(screenSize.y / 5 - screenSize.y / 3)
                        .alpha(1)
                        .setDuration(1000)
        };

        for (ViewPropertyAnimator x : animators) x.start();

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFBCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFBCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                fbContinueLogin(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                showInfo(R.string.error_contact_facebook);
            }
        });
    }

    @OnClick(R.id.login)
    public void login() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    public void fbContinueLogin(final LoginResult loginResult) {
        new ActionChain(Helpers.getThreadPolicy(WelcomeActivity.this, Executors.newFixedThreadPool(2))
        ).fail(errorHolder -> {
            showInfo(R.string.error_contact_facebook);
            errorHolder.getCause().printStackTrace();
        }).netThen(obj -> {
            AccessToken token = loginResult.getAccessToken();
            return chainFactory.get(fbUserInfoTask, token).start();
        }).fail(errorHolder -> {
            showInfo("Oops cannot connect to server.");
            errorHolder.getCause().printStackTrace();
        }).netThen((FbUserInfo info) -> {
            name = info.userName;
            return chainFactory.get(loginTask, info.accessToken.getToken()).start();
        }).netThen(obj -> {
            return chainFactory.get(userInfoTask, name).start();
        }).uiThen((Person name) -> {
            toMainActivity(new UserInfoUpdatedEvent(name));
            return null;
        }).start(null);
    }

    @Subscribe
    public void toMainActivity(UserInfoUpdatedEvent event) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("username", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }

    public void showInfo(String info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    public void showInfo(int info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
