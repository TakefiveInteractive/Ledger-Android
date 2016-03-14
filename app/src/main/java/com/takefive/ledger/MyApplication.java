package com.takefive.ledger;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.takefive.ledger.client.BusinessLedgerServiceModule;
import com.takefive.ledger.database.BusinessRealmModule;
import com.takefive.ledger.database.BusinessUserStoreModule;
import com.takefive.ledger.task.BusProvider;
import com.takefive.ledger.util.ChainFactoryProvider;

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
                new BusinessRealmModule(),
                new BusinessLedgerServiceModule(),
                new BusProvider(),
                new ChainFactoryProvider()
        );
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
