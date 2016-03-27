package com.takefive.ledger.view.database;

/**
 * Created by @tourbillon on 3/25/16.
 */
public class SessionStore {

    private static SessionStore instance = null;

    public static SessionStore getDefault() {
        if (instance == null)
            instance = new SessionStore();
        return instance;
    }

    public boolean initialized = false;

}
