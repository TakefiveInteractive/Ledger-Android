package com.takefive.ledger.presenter.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.takefive.ledger.R;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.ReadOnlyChain;
import zyu19.libs.action.chain.config.Producer;
import zyu19.libs.action.chain.config.PureAction;

/**
 * Created by zyu on 3/14/16.
 */
public class RealmAccess implements PureAction<PureAction<Realm, ?>, ReadOnlyChain> {

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
     * @param producer
     * @throws Exception
     */
    public <Out> Out clearAndDo(@Nullable Producer<Out> producer) throws Exception {
        return LambdaLock.lockAndDo(lockNotLoggedIn.writeLock(), () -> {
            // prevent future Realm Access
            notLoggedIn[0] = true;
            Realm.deleteRealm(startRealmConf.get().build());

            if(producer == null)
                return null;
            return producer.produce();
        });
    }

    public void enableAccess() throws Exception {
        LambdaLock.lockAndDo(lockNotLoggedIn.writeLock(), () -> {
            notLoggedIn[0] = false;
            return null;
        });
    }

    @Override
    public ReadOnlyChain process(PureAction<Realm, ?> editor) {
        return chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(() -> LambdaLock.lockAndDo(lockNotLoggedIn.readLock(), () -> {
            if (notLoggedIn[0])
                throw new IOException("[Internal] Invalid access to data before logging in.");
            Realm realm = null;
            try {
                realm = Realm.getInstance(startRealmConf.get()          // the name is configured inside Provider
                        .build());
                Object result = editor.process(realm);
                if (!realm.isClosed())
                    realm.close();
                return result;
            } catch (Exception err) {
                if (realm != null && realm.isInTransaction())
                    realm.cancelTransaction();
                throw err;
            }
        })).start();
    }

}
