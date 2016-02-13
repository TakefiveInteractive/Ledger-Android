package com.takefive.ledger.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.takefive.ledger.R;
import com.takefive.ledger.model.Person;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Module;
import io.realm.Realm;

/**
 * Created by @tourbillon on 2/2/16.
 */

public class BusinessUserStore implements UserStore {

    private Context context;
    private SharedPreferences preferences;

    @Inject
    public BusinessUserStore(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(
                context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
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
    public String getMostRecentUserId() {
        return preferences.getString(context.getString(R.string.preferences_identifier_userid), "");
    }

    @Override
    public String getMostRecentAccessToken() {
        return preferences.getString(context.getString(R.string.preferences_identifier_accessToken), "");
    }
}
