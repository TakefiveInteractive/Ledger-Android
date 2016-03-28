package com.takefive.ledger.dagger.userstore;

import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.view.MainActivity;
import com.takefive.ledger.view.MainNavFrag;
import com.takefive.ledger.view.WelcomeActivity;

import dagger.Module;
import dagger.Provides;

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
