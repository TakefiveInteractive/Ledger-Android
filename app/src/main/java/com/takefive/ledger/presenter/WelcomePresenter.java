package com.takefive.ledger.presenter;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.login.LoginResult;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.IPresenter;
import com.takefive.ledger.R;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.model.db.Person;
import com.takefive.ledger.presenter.client.LedgerService;
import com.takefive.ledger.presenter.database.RealmAccess;
import com.takefive.ledger.presenter.database.UserStore;
import com.takefive.ledger.view.WelcomeActivity;

import java.io.IOException;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import retrofit2.Response;
import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 3/19/16.
 */
public class WelcomePresenter implements IPresenter<WelcomeActivity> {
    WelcomeActivity view = null;

    @Inject
    CommonTasks tasks;

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    RealmAccess realmAccess;

    @Inject
    UserStore userStore;

    @Inject
    LedgerService service;

    @Override
    public void attachView(WelcomeActivity view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    public void ledgerLogin(final LoginResult fbLoginResult) {
        chainFactory.get(errorHolder -> {
            view.showInfo(R.string.error_contact_facebook);
            errorHolder.getCause().printStackTrace();
        }).netThen(obj -> {
            AccessToken token = fbLoginResult.getAccessToken();
            return tasks.getFbUserInfo(token);
        }).fail(errorHolder -> {
            view.showInfo("Oops cannot connect to server.");
            errorHolder.getCause().printStackTrace();
        }).netThen((FbUserInfo info) -> {
            tasks.getAndSaveAccessToken(info.accessToken.getToken());
            Response<RawPerson> me = service.getCurrentPerson().execute();
            if(!me.isSuccessful())
                throw new IOException(me.errorBody().string());
            userStore.setUserId(me.body()._id);
            tasks.saveRawPersonInRealm(me.body());
            return info.userName;
        }).uiThen((String username) -> {
            view.onLoginSuccess(username);
            return null;
        }).start(null);
    }
}
