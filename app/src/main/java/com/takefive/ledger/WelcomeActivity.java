package com.takefive.ledger;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
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
import com.takefive.ledger.task.InfoUpdatedEvent;
import com.takefive.ledger.task.LoginEvent;
import com.takefive.ledger.task.LoginTask;
import com.takefive.ledger.task.TaskFailEvent;
import com.takefive.ledger.task.UpdateUserInfoTask;
import com.takefive.ledger.task.UserInfoUpdatedEvent;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import zyu19.libs.action.chain.ActionChain;

public class WelcomeActivity extends AppCompatActivity {

    @Bind(R.id.backgroundImage)
    ImageView mBgImg;

    @Bind(R.id.login)
    BootstrapButton mLogin;

    @Bind(R.id.nameTag)
    TextView mNameTag;

    CallbackManager mFBCallbackManager;

    @Inject UserStore userStore;

    @Inject Realm realm;

    @Inject LedgerService service;

    @Inject Bus bus;

    @Inject Provider<LoginTask> loginTaskProvider;

    @Inject UpdateUserInfoTask userInfoTask;

    @Inject Provider<FbUserInfoTask> fbUserInfoProvider;

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

        Helpers.setMargins(mNameTag, screenSize.y/3, null, null, null);
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

        for(ViewPropertyAnimator x : animators) x.start();

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFBCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFBCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                fbUserInfoProvider.get().execute(loginResult.getAccessToken());
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

    @Subscribe
    public void retrieveInfo(LoginEvent event) {
        if (event.isSuccess()) {
            new ActionChain(Helpers.getThreadPolicy(this, Executors.newFixedThreadPool(2)))
            .uiThen(obj -> name)
            .fail(errorHolder -> showInfo("Oops cannot connect to server."))
            .use(userInfoTask)
            .uiThen((Person name) -> {
                toMainActivity(new UserInfoUpdatedEvent(name));
                return null;
            })
            .start(null);
        }
        else
            showInfo("Oops, couldn't connect to server...");
    }

    @Subscribe
    public void toMainActivity(UserInfoUpdatedEvent event) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("username", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void updateFbUserInfo(FbUserInfo info) {
        name = info.userName;
        loginTaskProvider.get().execute(info.accessToken.getToken());
    }

    @Subscribe
    public void missFbUserInfo(TaskFailEvent<FbUserInfoTask> event) {
        showInfo(R.string.error_contact_facebook);
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
