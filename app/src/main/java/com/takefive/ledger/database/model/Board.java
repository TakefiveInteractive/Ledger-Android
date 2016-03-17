package com.takefive.ledger.database.model;

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
    private com.takefive.ledger.model.Board content;

    public String getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public com.takefive.ledger.model.Board getRaw() {
        return content;
    }

    public void setFromRaw(com.takefive.ledger.model.Board board) {
        this.content = board;
    }
}
