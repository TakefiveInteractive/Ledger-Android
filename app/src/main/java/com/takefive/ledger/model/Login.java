package com.takefive.ledger.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class Login extends RealmObject {
    private String accessToken;
    private Date time;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
