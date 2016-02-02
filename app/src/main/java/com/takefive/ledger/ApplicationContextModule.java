package com.takefive.ledger;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by @tourbillon on 2/1/16.
 */

@Module(library = true)
public class ApplicationContextModule {

    private Context context;

    public ApplicationContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() { return context; }
}
