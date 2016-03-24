package com.takefive.ledger;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.takefive.ledger.presenter.client.BusinessLedgerServiceModule;
import com.takefive.ledger.presenter.database.BusinessUserStoreModule;
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
                new ChainFactoryProvider()
        );
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
