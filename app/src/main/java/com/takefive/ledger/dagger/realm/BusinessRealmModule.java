package com.takefive.ledger.dagger.realm;

import android.content.Context;

import com.takefive.ledger.presenter.MainPresenter;
import com.takefive.ledger.presenter.WelcomePresenter;
import com.takefive.ledger.view.MainActivity;
import com.takefive.ledger.view.WelcomeActivity;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmConfiguration;

/**
 * Created by zyu on 4/2/16.
 */
@Module(
        complete = false,
        library = true,
        injects = {WelcomePresenter.class, MainPresenter.class}
)
public class BusinessRealmModule {
    @Provides
    public RealmConfiguration.Builder provideBuilder(Context context) {
        return new RealmConfiguration.Builder(context);
    }
}
