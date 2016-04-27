package com.takefive.ledger.presenter;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.midData.ledger.RawBill;
import com.takefive.ledger.view.IBillDetail;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by @tourbillon on 4/20/16.
 */
public class BillDetailPresenter implements IPresenter<IBillDetail> {

    @Inject
    ILedgerService service;

    @Inject
    CommonTasks common;

    @Inject
    ActionChainFactory chainFactory;

    IBillDetail view;

    @Override
    public void attachView(IBillDetail view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    public void inflateBillDetail(String billId) {
        chainFactory.get(fail -> fail.getCause().printStackTrace())
            .netThen(() -> {
                Response<RawBill> response = service.getBill(billId).execute();
                if (!response.isSuccessful()) {
                    String msg = response.errorBody().string();
                    response.errorBody().close();
                    throw new IOException(msg);
                }
                RawBill bill = response.body();
                return common.billToBillInflated(bill);
            })
            .uiConsume(view::showAmounts)
            .start();
    }

}
