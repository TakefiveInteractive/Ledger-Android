package com.takefive.ledger;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.takefive.ledger.client.LedgerService;
import com.takefive.ledger.database.UserStore;
import com.takefive.ledger.model.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).inject(this);

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
                userStore.setAccessToken(loginResult.getAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("debug", object.toString());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        try {
                            realm.beginTransaction();
                            Person result = realm.where(Person.class)
                                    .equalTo("facebookId", loginResult.getAccessToken().getUserId())
                                    .findFirst();
                            if (result == null)
                                result = realm.createObject(Person.class);
                            result.setName(object.getString("name"));
                            result.setFacebookId(loginResult.getAccessToken().getUserId());
                            realm.commitTransaction();
                            userStore.setUserId(result.getPersonId());
                            intent.putExtra("username", object.getString("name"));

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Iterator<String> keys = object.keys();
                            while(keys.hasNext())
                                Log.d("object properties", keys.next());
                            e.printStackTrace();
                            Snackbar.make(findViewById(android.R.id.content), "Failed to contact Facebook.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                Bundle params = new Bundle();
                params.putString("fields", "name");
                request.setParameters(params);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                ;
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(findViewById(android.R.id.content), "Failed to contact Facebook.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.login)
    public void login() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
