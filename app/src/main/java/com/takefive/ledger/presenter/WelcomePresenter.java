package com.takefive.ledger.presenter;

import android.content.Context;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.IFbFactory;
import com.takefive.ledger.dagger.IFbLoginResult;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.view.WelcomeActivity;

import javax.inject.Inject;

import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 3/19/16.
 */
public class WelcomePresenter implements IPresenter<WelcomeActivity> {
    WelcomeActivity view = null;

    @Inject
    CommonTasks tasks;

    @Inject
    Context applicationContext;

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    RealmAccess realmAccess;

    @Inject
    UserStore userStore;

    @Inject
    ILedgerService service;

    @Inject
    IFbFactory fbFactory;

    @Override
    public void attachView(WelcomeActivity view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    // TODO: decide how to separate Facebook API from Presenters. Maybe another layer of abstraction?
    public void ledgerLogin(final IFbLoginResult fbLoginResult) {
        chainFactory.get(errorHolder -> {
            view.showAlert(R.string.error_contact_facebook);
            errorHolder.getCause().printStackTrace();
        }).netThen(obj -> {
            return tasks.getMyFbUserInfo(fbFactory.newRequest(fbLoginResult));
        }).fail(errorHolder -> {
            view.showAlert(applicationContext.getString(R.string.network_failure));
            errorHolder.getCause().printStackTrace();
        }).netThen((FbUserInfo info) -> {
            tasks.getAndSaveAccessToken(fbLoginResult.getTokenString());
            tasks.getAndSyncMyUserInfo();
            return info.userName;
        }).uiThen((String username) -> {
            view.onLoginSuccess(username);
            return null;
        }).start(null);
    }
}
