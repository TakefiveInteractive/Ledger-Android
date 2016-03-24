package com.takefive.ledger.presenter;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.takefive.ledger.IPresenter;
import com.takefive.ledger.model.RawBill;
import com.takefive.ledger.model.RawBoard;
import com.takefive.ledger.model.RawMyBoards;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.presenter.client.LedgerService;
import com.takefive.ledger.presenter.database.RealmAccess;
import com.takefive.ledger.presenter.database.UserStore;
import com.takefive.ledger.view.IMainView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.config.Consumer;
import zyu19.libs.action.chain.config.PureAction;

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
            view.showAlert("Cannot get boards: " + errorHolder.getCause().toString());
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
            view.showAlert("Cannot get boards: " + errorHolder.getCause().toString());
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

    /**
     * load and SYNC userInfo (Person and RawPerson)
     * @param userId the user id you want to query about
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

    public void loadUserFriends(Consumer<List<FbUserInfo>> callback) {
        chainFactory.get(fail -> fail.getCause().printStackTrace())
                .netThen(() -> {
                    GraphRequest request =
                            GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), null);
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,picture");
                    request.setParameters(parameters);
                    JSONArray response = request.executeAndWait().getJSONObject().getJSONArray("data");
                    List<FbUserInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < response.length(); ++i) {
                        JSONObject user = response.getJSONObject(i);
                        FbUserInfo info = new FbUserInfo();
                        info.id = user.getString("id");
                        info.name = user.getString("name");
                        info.avatarUrl = user.getJSONObject("picture").getJSONObject("data").getString("url");
                        infoList.add(info);
                    }
                    return infoList;
                })
                .uiConsume(callback)
                .start();
    }

}
