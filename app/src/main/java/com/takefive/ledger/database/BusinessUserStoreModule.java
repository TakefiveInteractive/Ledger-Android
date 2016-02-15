package com.takefive.ledger.database;

import android.content.Context;

import com.takefive.ledger.ApplicationContextProvider;
import com.takefive.ledger.MainActivity;
import com.takefive.ledger.MainNavFrag;
import com.takefive.ledger.WelcomeActivity;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * Created by @tourbillon on 2/2/16.
 */

@Module(
        complete = false,
        library = true,
        injects = {WelcomeActivity.class, MainActivity.class, MainNavFrag.class}
)
public class BusinessUserStoreModule {

    @Provides
    public UserStore provideUserStore(BusinessUserStore userStore) {
        return userStore;
    }
}
