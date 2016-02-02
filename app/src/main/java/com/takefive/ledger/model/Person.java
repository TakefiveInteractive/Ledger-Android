package com.takefive.ledger.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class Person extends RealmObject {
    @PrimaryKey
    private String personId;
    private String name;
    private Photo avatar;
    private String facebookId;
    private RealmList<Bill> bills;
    private RealmList<Board> boards;
    private Date createdAt;

    public Photo getAvatar() {
        return avatar;
    }

    public void setAvatar(Photo avatar) {
        this.avatar = avatar;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
