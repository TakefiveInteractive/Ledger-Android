package com.takefive.ledger.database;

import android.content.Context;

import com.takefive.ledger.ApplicationContextModule;
import com.takefive.ledger.R;
import com.takefive.ledger.WelcomeActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by @tourbillon on 2/1/16.
 */

@Module(includes = ApplicationContextModule.class, injects = WelcomeActivity.class)
public class RealmModule {

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
