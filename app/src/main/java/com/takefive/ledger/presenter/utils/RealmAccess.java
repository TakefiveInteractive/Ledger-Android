package com.takefive.ledger.presenter.utils;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.ReadOnlyChain;
import zyu19.libs.action.chain.config.PureAction;

/**
 * Created by zyu on 3/14/16.
 */
public class RealmAccess implements PureAction<PureAction<Realm, ?>, ReadOnlyChain> {

    public final static String DEFAULT_REALM_FILENAME = "ledger.realm";

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    Provider<RealmConfiguration.Builder> startRealmConf;

    @Override
    public ReadOnlyChain process(PureAction<Realm, ?> editor) {
        return chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(() -> {
            Realm realm = null;
            try {
                realm = Realm.getInstance(startRealmConf.get()
                        .name(DEFAULT_REALM_FILENAME)
                        .deleteRealmIfMigrationNeeded()
                        .build());
                Object result = editor.process(realm);
                realm.close();
                return result;
            } catch (Exception err) {
                if(realm != null && realm.isInTransaction())
                    realm.cancelTransaction();
                throw err;
            }
        }).start();
    }

}
