package com.takefive.ledger.dagger.fb;

import android.os.Parcelable;

import com.facebook.AccessToken;
import com.takefive.ledger.dagger.IFbLoginResult;

/**
 * Created by zyu on 3/28/16.
 */
public class BusinessFbLoginResult implements IFbLoginResult {
    AccessToken token = null;

    @Override
    public void setToken(Parcelable token) {
        token = (AccessToken) token;
    }

    @Override
    public Parcelable getToken() {
        return token;
    }

    @Override
    public String getTokenString() {
        return token.getToken();
    }
}
