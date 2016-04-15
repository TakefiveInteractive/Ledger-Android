package com.takefive.ledger.midData.ledger;

import com.takefive.ledger.midData.Money;

/**
 * Created by @tourbillon on 4/8/16.
 */
public class PersonAmountPair {

    public String person;

    public Money balance;

    public PersonAmountPair(String person, Money balance) {
        this.person = person;
        this.balance = balance;
    }
}
