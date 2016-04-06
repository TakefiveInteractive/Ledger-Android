package com.takefive.ledger.dagger.realm;

import android.content.Context;

import com.takefive.ledger.R;
import com.takefive.ledger.presenter.MainPresenter;
import com.takefive.ledger.presenter.WelcomePresenter;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmConfiguration;

/**
 * Created by zyu on 4/2/16 on zyu branch.
 * Created by zyu on 4/5/16 on master, again.
 */
@Module (
        complete = false,
        library = true,
        injects = {WelcomePresenter.class, MainPresenter.class}
)
public class BusinessRealmModule {
    @Provides
    public RealmConfiguration.Builder providerBuilder(Context context) {
        return new RealmConfiguration.Builder(context)
                .name(context.getString(R.string.realm_filename));
    }
}
