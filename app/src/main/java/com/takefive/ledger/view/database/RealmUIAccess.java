package com.takefive.ledger.view.database;

import android.content.Context;

import com.takefive.ledger.R;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.ReadOnlyChain;
import zyu19.libs.action.chain.config.PureAction;

/**
 * Created by @tourbillon on 3/25/16.
 */
public class RealmUIAccess implements PureAction<PureAction<Realm, ?>, ReadOnlyChain> {

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    Context context;

    @Override
    public ReadOnlyChain process(PureAction<Realm, ?> editor) {
        return chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).uiThen(() -> {
            Realm realm = null;
            try {
                realm = Realm.getInstance(new RealmConfiguration.Builder(context)
                        .name(context.getString(R.string.realm_filename))
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
