package com.takefive.ledger.database;

import android.content.Context;

import com.takefive.ledger.ApplicationContextProvider;
import com.takefive.ledger.MainActivity;
import com.takefive.ledger.MainNavFrag;
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

@Module(
        complete = false,
        library = true,
        injects = {WelcomeActivity.class, MainActivity.class, MainNavFrag.class}
)
public class BusinessRealmModule {

    @Provides
    @Singleton
    public Realm provideRealm(Context context) {
        RealmConfiguration configuration = new RealmConfiguration.Builder(context)
                .name(context.getString(R.string.realm_filename))
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        return Realm.getDefaultInstance();
    }
}
