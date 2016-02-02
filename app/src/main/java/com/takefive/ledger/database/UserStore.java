package com.takefive.ledger.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.takefive.ledger.R;
import com.takefive.ledger.model.Person;

import javax.inject.Inject;

import io.realm.Realm;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class UserStore {

    private Context context;
    private SharedPreferences preferences;
    private Realm realm;

    @Inject
    public UserStore(Context context, Realm realm) {
        this.context = context;
        this.preferences = context.getSharedPreferences(
                context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        this.realm = realm;
    }

    /*
    //TODO:
    public boolean isLoggedIn() {

    }
    */

    public void setUserId(String userId) {
        preferences.edit().putString(
                context.getString(R.string.preferences_identifier_userid), userId
        ).commit();
    }

    public void setAccessToken(String accessToken) {
        preferences.edit().putString(
                context.getString(R.string.preferences_identifier_accessToken), accessToken
        ).commit();
    }

    public String getMostRecentUserId() {
        return preferences.getString(
                context.getString(R.string.preferences_identifier_userid), "");
    }

    public String getMostRecentAccessToken() {
        return preferences.getString(
                context.getString(R.string.preferences_identifier_accessToken), "");
    }

    public Person getCurrentUser() {
        return realm.where(Person.class).equalTo("personId", getMostRecentUserId()).findFirst();
    }
}
