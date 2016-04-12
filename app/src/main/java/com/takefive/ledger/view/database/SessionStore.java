package com.takefive.ledger.view.database;

/**
 * Application Session Storage
 *
 * This class stores the session state (since last time it is open).
 * Must call {@code initialize} a user logs in.
 *
 * <bf>Note:</bf> User information is stored using Android's
 * native persistent storage. See {@link com.takefive.ledger.dagger.UserStore}.
 */
public class SessionStore {

    private static SessionStore instance = null;

    // Whether the app has completed UI initialization
    public boolean initialized;

    // Currently active (displayed) board's ID
    public String activeBoardId;

    // Currently active (displayed) board's name
    public String activeBoardName;

    public synchronized static SessionStore getDefault() {
        if (instance == null)
            instance = new SessionStore();
        return instance;
    }

    public synchronized static SessionStore initialize() {
        instance = new SessionStore();
        return instance;
    }

    // Initialize all variables
    private SessionStore() {
        initialized = false;
        activeBoardId = null;
        activeBoardName = null;
    }

}
