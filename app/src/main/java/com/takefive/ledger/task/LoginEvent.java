package com.takefive.ledger.task;

/**
 * Created by @tourbillon on 2/3/16.
 */
public class LoginEvent implements InfoUpdatedEvent<String> {

    private String accessToken;
    private boolean success;

    public LoginEvent(boolean success, String accessToken) {
        this.accessToken = accessToken;
        this.success = success;
    }

    @Override
    public String getUpdate() {
        return accessToken;
    }

    public boolean isSuccess() {
        return success;
    }
}
