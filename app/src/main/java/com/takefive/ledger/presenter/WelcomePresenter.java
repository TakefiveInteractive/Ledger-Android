package com.takefive.ledger.presenter;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.IFbFactory;
import com.takefive.ledger.dagger.IFbLoginResult;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.midData.fb.FbUserInfo;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.view.IWelcomeView;

import javax.inject.Inject;

import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 3/19/16.
 */
public class WelcomePresenter implements IPresenter<IWelcomeView> {
    IWelcomeView view = null;

    @Inject
    CommonTasks tasks;

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
    public void attachView(IWelcomeView view) {
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
            view.afterLoginFailure();
            errorHolder.getCause().printStackTrace();
        }).netThen(obj -> {
            realmAccess.enableAccess();
            return fbFactory.newRequest(fbLoginResult).getMe();
        }).fail(errorHolder -> {
            view.showAlert(R.string.network_failure);
            view.afterLoginFailure();
            errorHolder.getCause().printStackTrace();
        }).netThen((FbUserInfo info) -> {
            tasks.getAndSaveAccessToken(fbLoginResult.getTokenString());
            tasks.getAndSyncMyUserInfo();
            return info.name;
        }).uiThen((String username) -> {
            view.onLoginSuccess(username);
            return null;
        }).start(null);
    }
}
