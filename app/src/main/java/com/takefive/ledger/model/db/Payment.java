package com.takefive.ledger.model.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/2/16.
 * Todo: 3/17/16 Modify this to become DAO and to unify Models, after we have a general Model object for Payment
 */
public class Payment extends RealmObject {
    @PrimaryKey
    private String id;
    private Person from;

    public Person getTo() {
        return to;
    }

    public void setTo(Person to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person getFrom() {
        return from;
    }

    public void setFrom(Person from) {
        this.from = from;
    }

    private Person to;
}
