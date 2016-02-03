package com.takefive.ledger.database;

import com.takefive.ledger.model.Person;

/**
 * Created by @tourbillon on 2/1/16.
 */
public interface UserStore {

    /*
    //TODO:
    boolean isLoggedIn();
    */

    void setUserId(String userId);

    void setAccessToken(String accessToken);

    String getMostRecentUserId();

    String getMostRecentAccessToken();

    Person getCurrentUser();
}
