package com.takefive.ledger.presenter;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.IFbFactory;
import com.takefive.ledger.dagger.IFbLoginResult;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.midData.fb.FbUserInfo;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.model.Person;
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
    public void ledgerLogin() {
        chainFactory.get(errorHolder -> {
            view.showAlert(R.string.network_failure);
            view.afterLoginFailure();
            errorHolder.getCause().printStackTrace();
        }).netConsume(obj -> realmAccess.enableAccess()
        ).netThen(obj -> tasks.getAndSyncMyUserInfo()
        ).uiThen((Person person) -> {
            view.onLoginSuccess(person.getName());
            return null;
        }).start(null);
    }
}
