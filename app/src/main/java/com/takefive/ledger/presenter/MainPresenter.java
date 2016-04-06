package com.takefive.ledger.presenter;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.IFbFactory;
import com.takefive.ledger.dagger.IFbLoginResult;
import com.takefive.ledger.midData.fb.FbUserInfo;
import com.takefive.ledger.midData.ledger.RawBoard;
import com.takefive.ledger.midData.ledger.RawMyBoards;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.midData.ledger.NewBoardRequest;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.midData.view.ShownBill;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.view.IMainView;


import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.config.Consumer;

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
    ILedgerService service;

    @Inject
    CommonTasks tasks;

    @Inject
    IFbFactory fbFactory;

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
            view.stopRefreshing();
            view.showAlert("Cannot get boards: " + errorHolder.getCause().toString());
            errorHolder.getCause().printStackTrace();
        }).netThen(() -> {
            Response<RawBoard> resp = service.getBoardByIdInflated(boardId).execute();
            if (!resp.isSuccessful()) {
                String msg = resp.errorBody().string();
                resp.errorBody().close();
                throw new IOException(msg);
            }
            tasks.syncBoardInfo(resp.body());
            return tasks.inflateBills(resp.body().bills);
        }).uiConsume((List<ShownBill> bills) -> view.showBillsList(bills)
        ).uiThen(() -> {
            view.stopRefreshing();
            return null;
        }).start();
    }

    public void loadMyBoards() {
        chainFactory.get(errorHolder -> {
            view.showAlert("Cannot get boards: " + errorHolder.getCause().toString());
            errorHolder.getCause().printStackTrace();
        }).netThen(() -> {
            Response<RawMyBoards> resp = service.getMyBoards().execute();
            if (!resp.isSuccessful()) {
                String msg = resp.errorBody().string();
                resp.errorBody().close();
                throw new IOException(msg);
            }
            tasks.syncMyBoardsInfo(resp.body());
            return resp.body();
        }).uiConsume(view::showMyBoards).start();
    }

    public void loadMyUserInfo() {
        chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(tasks::getAndSyncMyUserInfo
        ).uiConsume(view::showMyUserInfo).start();
    }

    /**
     * load and SYNC userInfo (Person and RawPerson)
     *
     * @param userId   the user id you want to query about
     * @param callback it will be called on UI thread
     */
    public void loadUserInfo(String userId, Consumer<RawPerson> callback) {
        chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(() -> {
            Response<RawPerson> response = service.getPerson(userId).execute();
            if (!response.isSuccessful())
                throw new IOException(response.errorBody().string());
            tasks.syncUserInfo(response.body());
            return response.body();
        }).uiConsume(callback).start();
    }

    public void loadUserFriends(IFbLoginResult loginResult, Consumer<List<FbUserInfo>> callback) {
        chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(() -> {
            return fbFactory.newRequest(loginResult).getMyFriends();
        }).uiConsume(callback
        ).start();
    }

    public void createBoard(NewBoardRequest request, Consumer<Response> callback) {
        chainFactory.get(fail -> {
            fail.getCause().printStackTrace();
            view.showAlert(R.string.network_failure);
        }).netThen(() -> {
            Response<ResponseBody> response = service.createBoard(request).execute();
            return response;
        }).uiConsume(callback
        ).start();
    }

    public void refreshBoardInfo(RawMyBoards.Entry entry) {
        // TODO: actually reload board? Otherwise why is this in Presenter?
        view.setBoardTitle(entry.name);
        view.setCurrentBoardId(entry.id);
    }

    public void logout() {
        chainFactory.get(fail -> {
            fail.getCause().printStackTrace();
            view.showAlert(R.string.ex_cannot_logout);
        }).netThen(() -> realmAccess.clearAndDo(() -> {
            userStore.clearAll();
            return null;
        })).uiConsume(obj -> view.finishLogout()
        ).start();
    }

}
