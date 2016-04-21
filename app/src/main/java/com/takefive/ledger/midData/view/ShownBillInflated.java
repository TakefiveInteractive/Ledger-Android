package com.takefive.ledger.midData.view;

import android.support.v4.util.Pair;

import com.takefive.ledger.midData.ledger.RawBill;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.model.Person;

import java.util.List;

/**
 * Created by @tourbillon on 4/20/16.
 */
public class ShownBillInflated {

    public RawBill rawBill = null;
    public List<ShownAmount> amounts = null;

    public String recipientName = null;
    public String recipientAvatarUrl = null;
}
