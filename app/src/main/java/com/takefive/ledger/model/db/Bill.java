package com.takefive.ledger.model.db;

import com.takefive.ledger.model.RawBill;


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

    public String getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
