package com.takefive.ledger.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by @tourbillon on 3/28/16.
 */
public class MyBoards extends RealmObject {

    private RealmList<Entry> boards;

    public RealmList<Entry> getBoards() {
        return boards;
    }

    public void setBoards(RealmList<Entry> boards) {
        this.boards = boards;
    }
}
