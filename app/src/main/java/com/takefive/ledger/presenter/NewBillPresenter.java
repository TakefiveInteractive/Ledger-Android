package com.takefive.ledger.presenter;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.midData.ledger.NewBillRequest;
import com.takefive.ledger.midData.ledger.RawBoard;
import com.takefive.ledger.midData.ledger.RawBoardSimple;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.model.Board;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.view.INewBill;
import com.takefive.ledger.view.database.SessionStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.realm.RealmQuery;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import okhttp3.ResponseBody;
import retrofit2.Response;
import zyu19.libs.action.chain.AbstractActionChain;
import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.ReadOnlyChain;
import zyu19.libs.action.chain.TActionChain;
import zyu19.libs.action.chain.TActionChainFactory;
import zyu19.libs.action.chain.config.Consumer;

/**
 * Created by @tourbillon on 4/4/16.
 */
public class NewBillPresenter implements IPresenter<INewBill> {

    INewBill view = null;

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    ILedgerService service;

    @Inject
    RealmAccess realmAccess;

    @Inject
    CommonTasks tasks;

    @Override
    public void attachView(INewBill view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    public void loadBoardMembers(Consumer<List<RawPerson>> callback) {
        chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(() -> {
            Response<RawBoardSimple> response =
                    service.getBoardByIdSimple(SessionStore.getDefault().activeBoardId).execute();

            if (!response.isSuccessful()) {
                String msg = response.errorBody().string();
                response.errorBody().close();
                throw new IOException(msg);
            }

            return ActionChain.all(StreamSupport.stream(response.body().members
            ).map(member -> chainFactory.get(fail -> fail.getCause().printStackTrace()
            ).netThen(() -> {
                Response<RawPerson> personResponse = service.getPerson(member).execute();
                if (!response.isSuccessful()) {
                    String msg = response.errorBody().string();
                    response.errorBody().close();
                    throw new IOException(msg);
                }
                return personResponse.body();
            }).start()).collect(Collectors.toList()));
        }).uiConsume(callback
        ).start();
    }

    public void createBill(NewBillRequest request) {
        chainFactory.get(fail -> fail.getCause().printStackTrace())
                .netThen(() -> {
                    Response<ResponseBody> response = service.createBill(request).execute();
                    if (!response.isSuccessful()) {
                        String msg = response.errorBody().string();
                        response.errorBody().close();
                        throw new IOException(msg);
                    }
                    return null;
                })
                .uiConsume(r -> view.doneAndClose())
                .start();
    }
}
