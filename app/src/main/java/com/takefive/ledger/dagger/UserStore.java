package com.takefive.ledger.dagger;

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
