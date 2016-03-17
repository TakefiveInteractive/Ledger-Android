package com.takefive.ledger.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.takefive.ledger.R;

import javax.inject.Inject;

import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.config.ErrorHolder;
import zyu19.libs.action.chain.config.NiceConsumer;

/**
 * Created by @tourbillon on 2/2/16.
 */

public class BusinessUserStore implements UserStore {

    private Context context;
    private ActionChainFactory chainFactory;
    private SharedPreferences preferences;

    @Inject
    public BusinessUserStore(Context context, ActionChainFactory chainFactory) {
        this.context = context;
        this.chainFactory = chainFactory;
        this.preferences = context.getSharedPreferences(
                context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public ActionChain isLoggedIn(NiceConsumer<ErrorHolder> onFailure) {
        return chainFactory.get(onFailure
        ).netThen(() -> {
            if(!preferences.contains(context.getString(R.string.preferences_identifier_accessToken))
                    || !preferences.contains(context.getString(R.string.preferences_identifier_fbToken)))
                return false;
            String fbToken = getMostRecentFbToken();
            String ourToken = getMostRecentAccessToken();
            if(fbToken == null || ourToken == null)
                return false;
            return null;
        });
    }

    @Override
    public void setUserId(String userId) {
        preferences.edit().putString(
                context.getString(R.string.preferences_identifier_userid), userId
        ).commit();
    }

    @Override
    public void setAccessToken(String accessToken) {
        preferences.edit().putString(
                context.getString(R.string.preferences_identifier_accessToken), accessToken
        ).commit();
    }

    @Override
    public void setFbToken(String fbToken) {
        preferences.edit().putString(
                context.getString(R.string.preferences_identifier_fbToken), fbToken
        ).commit();
    }

    @Override
    public String getMostRecentUserId() {
        return preferences.getString(context.getString(R.string.preferences_identifier_userid), null);
    }

    @Override
    public String getMostRecentAccessToken() {
        return preferences.getString(context.getString(R.string.preferences_identifier_accessToken), null);
    }

    @Override
    public String getMostRecentFbToken() {
        return preferences.getString(context.getString(R.string.preferences_identifier_fbToken), null);
    }
}
