package com.takefive.ledger.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class Payment extends RealmObject {
    @PrimaryKey
    private String paymentId;
    private double amount;
    private Person fromPerson;
    private Person toPerson;
    private String targetVenmoId;
    private String venmoAudience;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Person getFromPerson() {
        return fromPerson;
    }

    public void setFromPerson(Person fromPerson) {
        this.fromPerson = fromPerson;
    }

    public Person getToPerson() {
        return toPerson;
    }

    public void setToPerson(Person toPerson) {
        this.toPerson = toPerson;
    }

    public String getTargetVenmoId() {
        return targetVenmoId;
    }

    public void setTargetVenmoId(String targetVenmoId) {
        this.targetVenmoId = targetVenmoId;
    }

    public String getVenmoAudience() {
        return venmoAudience;
    }

    public void setVenmoAudience(String venmoAudience) {
        this.venmoAudience = venmoAudience;
    }
}
