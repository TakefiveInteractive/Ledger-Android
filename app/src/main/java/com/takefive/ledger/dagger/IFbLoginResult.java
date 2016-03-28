package com.takefive.ledger.dagger;

import android.os.Parcelable;

/**
 * Created by zyu on 3/28/16.
 */
public interface IFbLoginResult {

    void setToken(Parcelable token);
    Parcelable getToken();
    String getTokenString();
}
