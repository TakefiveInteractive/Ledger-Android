package com.takefive.ledger.task;

import com.squareup.otto.Bus;
import com.takefive.ledger.WelcomeActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by @tourbillon on 2/3/16.
 */

@Module(library = true)
public class BusProvider {

    @Singleton
    @Provides
    public Bus provideBus() {
        return new Bus();
    }
}
