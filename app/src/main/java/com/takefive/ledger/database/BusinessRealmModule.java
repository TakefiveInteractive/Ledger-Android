package com.takefive.ledger.database;

import android.content.Context;

import com.takefive.ledger.ApplicationContextProvider;
import com.takefive.ledger.WelcomeActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * Created by @tourbillon on 2/1/16.
 */

@Module(
        complete = false,
        library = true,
        injects = {WelcomeActivity.class}
)
public class BusinessRealmModule {

    @Provides
    @Singleton
    public Realm provideRealm(Context context) {
        /*
        RealmConfiguration configuration = new RealmConfiguration.Builder(context)
                .name(context.getString(R.string.realm_filename))
                .build();
        Realm.setDefaultConfiguration(configuration);
        return Realm.getDefaultInstance();
        */
        return Realm.getInstance(context);
    }
}
