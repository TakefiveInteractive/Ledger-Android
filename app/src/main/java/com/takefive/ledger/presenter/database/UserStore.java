package com.takefive.ledger.presenter.database;

import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.config.ErrorHolder;
import zyu19.libs.action.chain.config.NiceConsumer;

/**
 * Created by @tourbillon on 2/1/16.
 */
public interface UserStore {

    /**
     * whether the two tokens exist.
     */
    boolean hasLoginInfo();

    void setUserId(String userId);

    void setAccessToken(String accessToken);

    void setFbToken(String fbToken);

    String getMostRecentUserId();

    String getMostRecentAccessToken();

    String getMostRecentFbToken();
}
