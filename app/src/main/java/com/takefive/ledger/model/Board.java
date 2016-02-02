package com.takefive.ledger.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class Board extends RealmObject {
    @PrimaryKey
    private String boardId;
    private Date createdAt;
    private String name;
    private boolean isActive;
    private Person creator;
    private RealmList<Person> members;
    private RealmList<Bill> bills;
    private boolean isCleared;
    private RealmList<Payment> payments;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public RealmList<Person> getMembers() {
        return members;
    }

    public void setMembers(RealmList<Person> members) {
        this.members = members;
    }

    public RealmList<Bill> getBills() {
        return bills;
    }

    public void setBills(RealmList<Bill> bills) {
        this.bills = bills;
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
        isCleared = cleared;
    }

    public RealmList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(RealmList<Payment> payments) {
        this.payments = payments;
    }
}
