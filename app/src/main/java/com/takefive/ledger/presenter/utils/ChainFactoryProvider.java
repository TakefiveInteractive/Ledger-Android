package com.takefive.ledger.presenter.utils;

import android.os.Handler;
import android.os.Looper;

import com.takefive.ledger.view.BillDetailActivity;
import com.takefive.ledger.view.MainActivity;
import com.takefive.ledger.view.MainBillFragment;
import com.takefive.ledger.view.MainNavFrag;
import com.takefive.ledger.view.NewBillActivity;
import com.takefive.ledger.view.NewBoardFragment;
import com.takefive.ledger.view.WelcomeActivity;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.TActionChainFactory;
import zyu19.libs.action.chain.config.ThreadPolicy;

/**
 * Created by zyu on 3/13/16.
 * This is a Business MODULE so it is allowed to use Android APIs.
 */

@Module(
        complete = false,
        library = true,
        injects = {
            WelcomeActivity.class,
            MainActivity.class,
            MainNavFrag.class,
            MainBillFragment.class,
            NewBoardFragment.class,
            NewBillActivity.class,
            BillDetailActivity.class
        }
)
public class ChainFactoryProvider {
    @Provides
    @Singleton
    public ActionChainFactory provide() {
        return new ActionChainFactory(new ThreadPolicy(runnable ->
                new Handler(Looper.getMainLooper()).post(runnable),
                Executors.newFixedThreadPool(2)
        ));
    }

    @Provides
    @Singleton
    public TActionChainFactory provideTypeSafe() {
        return new TActionChainFactory(new ThreadPolicy(runnable ->
                new Handler(Looper.getMainLooper()).post(runnable),
                Executors.newFixedThreadPool(2)
        ));
    }

}
