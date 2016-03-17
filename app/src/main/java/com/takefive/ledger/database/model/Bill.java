package com.takefive.ledger.database.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 * Modified by Zhongzhi Yu @c4phone on 3/17/16 to become DAO and to unify Model classes.
 */
public class Bill extends RealmObject {
    @PrimaryKey
    private String id;
    private long createTime;
    private com.takefive.ledger.model.Bill content;

    public String getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public com.takefive.ledger.model.Bill getRaw() {
        return content;
    }

    public void setFromRaw(com.takefive.ledger.model.Bill bill) {
        this.content = bill;
    }
}
