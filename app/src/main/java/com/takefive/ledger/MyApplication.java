package com.takefive.ledger;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.facebook.FacebookSdk;
import com.takefive.ledger.dagger.fb.BusinessFbFactoryProvider;
import com.takefive.ledger.dagger.ledger.BusinessLedgerServiceModule;
import com.takefive.ledger.dagger.realm.BusinessRealmModule;
import com.takefive.ledger.dagger.userstore.BusinessUserStoreModule;
import com.takefive.ledger.presenter.utils.ChainFactoryProvider;

import dagger.ObjectGraph;

/**
 * Created by zyu on 2/1/16.
 */
public class MyApplication extends Application {

    private ObjectGraph objectGraph;

    @Override public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
        this.objectGraph = ObjectGraph.create(
                new ApplicationContextProvider(this),
                new BusinessUserStoreModule(),
                new BusinessLedgerServiceModule(),
                new BusinessRealmModule(),
                new ChainFactoryProvider(),
                new BusinessFbFactoryProvider(),
                new BusinessRealmModule()
        );
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
