package com.takefive.ledger.view;

import com.takefive.ledger.IView;
import com.takefive.ledger.midData.Money;

/**
 * Created by zyu on 4/14/16.
 */
public interface INewBillAmountView extends IView {

    /*
     * For inputting data by hand, presenter should be notified.
     * !!!! However, View's updateAmountForPerson will not be called on that person.
     * (It might be called on other people due to this change though.)
     *
     */

    // Use a string rather than list position as id. Safer.
    void updateAmountForPerson(String id, Money amount);
    void updateRemainingAmount(Money amount);
}
