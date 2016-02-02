package com.takefive.ledger.model;

import io.realm.RealmObject;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class Amount extends RealmObject {
    private Person person;
    private double balance;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
