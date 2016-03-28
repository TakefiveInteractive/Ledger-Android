package com.takefive.ledger.model;

import io.realm.RealmObject;

/**
 * Created by @tourbillon on 3/28/16.
 */
public class Entry extends RealmObject {

    private String id;
    private String name;

    public Entry() {

    }

    public Entry(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
