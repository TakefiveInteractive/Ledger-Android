package com.takefive.ledger.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by @tourboardon on 2/1/16.
 * Modified by Zhongzhi Yu @c4phone on 3/17/16 to become DAO and to unify Model classes.
 */
public class Board extends RealmObject {
    @PrimaryKey
    private String boardId;
    @Required
    private String name;
    private boolean isActive;
    @Required
    private String creator;
    private RealmList<StringHolder> members;
    private long createdAt;

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String id) {
        this.boardId = id;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public RealmList<StringHolder> getMembers() {
        return members;
    }

    public void setMembers(RealmList<StringHolder> members) {
        this.members = members;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
