package com.takefive.ledger.presenter;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.IView;
import com.takefive.ledger.model.RawBill;
import com.takefive.ledger.model.RawBoard;
import com.takefive.ledger.model.RawMyBoards;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.presenter.client.LedgerService;
import com.takefive.ledger.presenter.database.RealmAccess;
import com.takefive.ledger.presenter.database.UserStore;
import com.takefive.ledger.view.IMainView;
import com.takefive.ledger.model.db.Person;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 3/19/16.
 */
public class MainPresenter implements IPresenter<IMainView> {
    @Inject
    ActionChainFactory chainFactory;

    @Inject
    RealmAccess realmAccess;

    @Inject
    UserStore userStore;

    @Inject
    LedgerService service;

    @Inject
    CommonTasks tasks;

    IMainView view = null;

    @Override
    public void attachView(IMainView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    public void loadBills(String boardId) {
        chainFactory.get(errorHolder -> {
            view.showInfo("Cannot get boards: " + errorHolder.getCause().toString());
            errorHolder.getCause().printStackTrace();
        }).netThen(() -> {
            Response<RawBoard> resp = service.getBoardById(boardId).execute();
            if (!resp.isSuccessful()) {
                String msg = resp.errorBody().string();
                resp.errorBody().close();
                throw new IOException(msg);
            }
            return resp.body().bills;
        }).uiConsume(view::showBillsList).start();
    }

    public void loadMyBoards() {
        chainFactory.get(errorHolder -> {
            view.showInfo("Cannot get boards: " + errorHolder.getCause().toString());
            errorHolder.getCause().printStackTrace();
        }).netThen(() -> {
            Response<RawMyBoards> resp = service.getMyBoards().execute();
            if (!resp.isSuccessful()) {
                String msg = resp.errorBody().string();
                resp.errorBody().close();
                throw new IOException(msg);
            }
            return resp.body();
        }).uiConsume(view::showMyBoards).start();
    }

    public void loadMyUserInfo() {
        chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(tasks::getAndSyncMyUserInfo
        ).uiConsume(view::showMyUserInfo).start();
    }
}
