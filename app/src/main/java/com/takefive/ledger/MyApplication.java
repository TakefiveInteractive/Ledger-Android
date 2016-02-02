package com.takefive.ledger;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.takefive.ledger.database.RealmModule;

import java.util.Arrays;

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
                new ApplicationContextModule(this),
                new RealmModule()
        );
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
