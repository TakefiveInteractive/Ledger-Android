package com.takefive.ledger.view;

import com.takefive.ledger.IView;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.midData.view.ShownBillInflated;

import java.util.List;

/**
 * Created by @tourbillon on 4/20/16.
 */
public interface IBillDetail extends IView {

    void showAmounts(ShownBillInflated bill);

}
