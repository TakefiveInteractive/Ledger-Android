package com.takefive.ledger.model.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/2/16.
 * Todo: 3/17/16 Modify this to become DAO and to unify Models, after we have a general Model object for Payment
 */
class Payment extends RealmObject {
    @PrimaryKey
    private String id;
    private Person from;
    private Person to;
}
