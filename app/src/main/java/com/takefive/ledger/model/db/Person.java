package com.takefive.ledger.model.db;

import com.takefive.ledger.model.RawPerson;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 * Modified by Zhongzhi Yu @c4phone on 3/17/16 to become DAO and to unify Model classes.
 * This is a complex DAO where setting some field will also change RAW Models.
 * Possible usage: updating the fields could immidiately update db, after which, calling getRaw() will result in
 *  a synchronized "raw" Model object
 */
public class Person extends RealmObject {
    @PrimaryKey
    private String personId;
    private RealmList<Bill> bills;
    private RealmList<Board> boards;
    private long createdAt;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public RealmList<Bill> getBills() {
        return bills;
    }

    public void setBills(RealmList<Bill> bills) {
        this.bills = bills;
    }

    public RealmList<Board> getBoards() {
        return boards;
    }

    public void setBoards(RealmList<Board> boards) {
        this.boards = boards;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
