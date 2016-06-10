package com.takefive.ledger.presenter.utils;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;
import rx.functions.Func1;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.config.Producer;

/**
 * Created by zyu on 3/14/16.
 */
public class RxRealmAccess {

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    Provider<RealmConfiguration.Builder> startRealmConf;

    // Are we not logged in yet, and thus not permitted to use Realm?
    //      At boot time, we are not logged in yet.
    final static boolean[] notLoggedIn = new boolean[]{true};
    final static ReentrantReadWriteLock lockNotLoggedIn = new ReentrantReadWriteLock();

    /**
     * Wait for all tasks to finish, and then clear the database, and then run "runnable"
     *
     * @param producer
     * @throws Exception
     */
    public <Out> Observable<Out> clearAndDo(@Nullable Producer<Out> producer) throws Exception {
        return RxLambdaLock.lockAndDo(lockNotLoggedIn.writeLock(), () -> {
            // prevent future Realm Access
            notLoggedIn[0] = true;
            Realm.deleteRealm(startRealmConf.get().build());

            if (producer == null)
                return null;
            return producer.produce();
        });
    }

    static public Observable<Void> enableAccess() {
        return RxLambdaLock.lockAndDo(lockNotLoggedIn.writeLock(), () -> {
            notLoggedIn[0] = false;
            return Observable.just(null);
        });
    }

    public <T> Observable<T> access(Func1<Realm, Observable<T>> editor) {
        return RxLambdaLock.<T>lockAndDo(lockNotLoggedIn.readLock(), () -> {
            if (notLoggedIn[0])
                return Observable.error(new IOException("[Internal] Invalid access to data before logging in."));
            Realm realm = null;
            try {
                realm = Realm.getInstance(startRealmConf.get()          // the name is configured inside Provider
                        .build());

                final Realm realm2 = realm;
                return editor.call(realm
                ).doOnCompleted(() -> {
                    if (realm2.isInTransaction())
                        realm2.commitTransaction();
                    if (!realm2.isClosed())
                        realm2.close();
                }).doOnError(error -> {
                    if (realm2 != null && realm2.isInTransaction())
                        realm2.cancelTransaction();
                    if (!realm2.isClosed())
                        realm2.close();
                });
            } catch (Exception err) {
                if (realm != null && realm.isInTransaction())
                    realm.cancelTransaction();
                if (!realm.isClosed())
                    realm.close();
                return Observable.error(err);
            }
        });
    }
}
