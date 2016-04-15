package com.takefive.ledger.presenter;

import android.support.annotation.NonNull;

import com.takefive.ledger.IPresenter;
import com.takefive.ledger.midData.Money;
import com.takefive.ledger.view.INewBillAmountView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zyu on 4/14/16.
 */
public class NewBillAmountPresenter implements IPresenter<INewBillAmountView> {

    @NonNull
    INewBillAmountView view;

    public NewBillAmountPresenter(Locale locale) {
        this.locale = locale;
        zero = new Money(locale, 0);
        totalAmount = zero;
    }

    final Locale locale;
    final Money zero;

    Money totalAmount;

    // data inputed by hand rather than automatically assigned.
    // Person id => assigned amount.
    HashMap<String, Money> handInput = new HashMap<>();

    // person id of those people who decide to automatically split money, evenly.
    Map<String, Money> autoSplit = new HashMap<>();

    synchronized public void setTotalAmount(Money amount) {
        if (totalAmount == amount)
            return;
        totalAmount = amount;
        update();
    }

    // can user press confirm button?
    synchronized public boolean canConfirm() {
        if (autoSplit.size() > 0) {
            for (Money m : autoSplit.values())
                if (m == null || m.isNegative())
                    return false;
            for (Money m : handInput.values())
                if (m == null || m.isNegative())
                    return false;
            return true;
        }
        if (totalAmount.equals(getTotalOfHandInput()))
            return true;
        return false;
    }

    // Use a string rather than list position as id. Safer.
    synchronized public void inputAmountForPerson(String id, Money amount) {
        if (handInput.containsKey(id))
            if (handInput.get(id).equals(amount))
                return;
        handInput.put(id, amount);
        update();
    }

    synchronized public void clearExceptTotalAmount() {
        handInput.clear();
        autoSplit.clear();
        update();
    }

    private void update() {
        Money remainingAmount = totalAmount.minus(getTotalOfHandInput());

        if (autoSplit.size() == 0) {
            view.updateRemainingAmount(remainingAmount);
        } else {
            // calculate share of every people who want to auto split
            Map<String, Money> share = remainingAmount.fairSplit(autoSplit.keySet());
            for (Map.Entry<String, Money> entry : share.entrySet())
                view.updateAmountForPerson(entry.getKey(), entry.getValue());
            autoSplit = share;

            view.updateRemainingAmount(zero);
        }
    }

    // Use a string rather than list position as id. Safer.
    synchronized public void enableAutomaticAmountFor(String id) {
        autoSplit.put(id, null);
        handInput.remove(id);

        // this will also update view for us.
        update();
    }

    // Use a string rather than list position as id. Safer.
    // After disabling, the automatically assigned amount will NOT disappear
    synchronized public void disableAutomaticAmountFor(String id) {
        if (!autoSplit.containsKey(id))
            return;

        Money hisShare = autoSplit.get(id);
        hisShare = hisShare == null ? zero : hisShare;
        handInput.put(id, hisShare);
        view.updateAmountForPerson(id, hisShare);

        autoSplit.remove(id);
        update();
    }

    synchronized public Map<String, Money> getAssignments() {
        HashMap<String, Money> ans = new HashMap<>();
        ans.putAll(handInput);
        ans.putAll(autoSplit);
        return ans;
    }

    //----------- helpers ------------

    // The total amount of people who do NOT automatically split.
    private Money getTotalOfHandInput() {
        Money ans = zero;
        for (Money val : handInput.values())
            ans = ans.plus(val);
        return ans;
    }

    @Override
    synchronized public void attachView(INewBillAmountView view) {
        this.view = view;
    }

    @Override
    synchronized public void detachView() {
        view = null;
    }
}
