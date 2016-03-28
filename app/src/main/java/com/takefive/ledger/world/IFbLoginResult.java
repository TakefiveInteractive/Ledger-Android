package com.takefive.ledger.world;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Provider;

import zyu19.libs.action.chain.config.NiceConsumer;

/**
 * Created by zyu on 3/28/16.
 */
public interface IFbLoginResult {

    void setToken(Parcelable token);
    Parcelable getToken();
    String getTokenString();
}
