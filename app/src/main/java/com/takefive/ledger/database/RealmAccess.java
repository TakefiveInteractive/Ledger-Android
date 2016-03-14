package com.takefive.ledger.database;

import android.content.Context;
import android.support.annotation.RequiresPermission;

import com.takefive.ledger.R;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Provider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.ReadOnlyChain;
import zyu19.libs.action.chain.config.NiceConsumer;
import zyu19.libs.action.chain.config.Producer;
import zyu19.libs.action.chain.config.PureAction;
import zyu19.libs.action.chain.config.ThreadPolicy;

/**
 * Created by zyu on 3/14/16.
 */
public class RealmAccess implements PureAction<PureAction<Realm, ?>, ReadOnlyChain> {

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    Context context;

    @Override
    public ReadOnlyChain process(PureAction<Realm, ?> editor) throws Exception {
        return chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(() -> {
            Realm realm = Realm.getInstance(new RealmConfiguration.Builder(context)
                    .name(context.getString(R.string.realm_filename))
                    .deleteRealmIfMigrationNeeded()
                    .build());
            Object result = editor.process(realm);
            realm.close();
            return result;
        }).start();
    }
}
