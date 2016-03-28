package com.takefive.ledger.model.db;

import io.realm.RealmObject;

/**
 * Created by @tourbillon on 3/28/16.
 */
public class StringHolder extends RealmObject {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
