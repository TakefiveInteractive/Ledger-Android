package com.takefive.ledger.model.db;

import com.takefive.ledger.model.RawBoard;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourboardon on 2/1/16.
 * Modified by Zhongzhi Yu @c4phone on 3/17/16 to become DAO and to unify Model classes.
 */
public class Board extends RealmObject {
    @PrimaryKey
    private String id;
    private long createTime;
    private RawBoard content;

    public String getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public RawBoard getRaw() {
        return content;
    }

    public void setFromRaw(RawBoard board) {
        this.content = board;
    }
}
