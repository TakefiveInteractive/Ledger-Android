package com.takefive.ledger.view.database;

/**
 * Created by @tourbillon on 3/25/16.
 */
public class SessionStore {

    private static SessionStore instance = null;

    public synchronized static SessionStore getDefault() {
        if (instance == null)
            instance = new SessionStore();
        return instance;
    }

    public boolean initialized = false;
    public String activeBoardId = null;
    public String activeBoardName = null;

}
