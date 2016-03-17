package com.takefive.ledger.database;

import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.config.ErrorHolder;
import zyu19.libs.action.chain.config.NiceConsumer;

/**
 * Created by @tourbillon on 2/1/16.
 */
public interface UserStore {

    // Return an action chain that contains a boolean
    ActionChain isLoggedIn(NiceConsumer<ErrorHolder> onFailure);

    void setUserId(String userId);

    void setAccessToken(String accessToken);

    void setFbToken(String fbToken);

    String getMostRecentUserId();

    String getMostRecentAccessToken();

    String getMostRecentFbToken();
}
