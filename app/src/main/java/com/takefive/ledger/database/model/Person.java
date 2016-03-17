package com.takefive.ledger.database.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 * Modified by Zhongzhi Yu @c4phone on 3/17/16 to become DAO and to unify Model classes.
 * This is a complex DAO where setting some field will also change RAW Models.
 * Possible usage: updating the fields could immidiately update db, after which, calling getRaw() will result in
 *  an object to send to LedgerService.updateBoard()
 */
public class Person extends RealmObject {
    @PrimaryKey
    private String personId;
    private RealmList<Bill> bills;
    private RealmList<Board> boards;
    private long createdAt;
    private com.takefive.ledger.model.Person person;

    public String getId() {
        return personId;
    }

    public RealmList<Bill> getBills() {
        return bills;
    }

    public RealmList<Board> getBoards() {
        return boards;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public com.takefive.ledger.model.Person getRaw() {
        return person;
    }

    public void setId(String personId) {
        this.personId = personId;
    }

    public void setBills(RealmList<Bill> bills) {
        this.bills = bills;
    }

    public void setBoards(RealmList<Board> boards) {
        this.boards = boards;
    }

    public void setFromRaw(com.takefive.ledger.model.Person person) {
        this.person = person;
    }
}
