package com.takefive.ledger.tests;

import android.content.Context;

import com.takefive.ledger.dagger.IFbFactory;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.presenter.WelcomePresenter;
import com.takefive.ledger.view.IWelcomeView;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.realm.RealmConfiguration;
import zyu19.libs.action.chain.ActionChainFactory;
import zyu19.libs.action.chain.config.ThreadPolicy;

/**
 * Created by zyu on 4/1/16.
 */
public class TestWelcome {

    ActionChainFactory chainFactory = new ActionChainFactory(new ThreadPolicy(
            null,
            Executors.newCachedThreadPool()
    ));

    @Before
    public void setup() {
    }


    @Test(timeout = 1000)
    public void willPrintError() {
        IWelcomeView view;
        WelcomePresenter[] pPresenter = new WelcomePresenter[1];
        AtomicBoolean finished = new AtomicBoolean(false);
        view = new IWelcomeView() {
            @Override
            public void onLoginSuccess(String username) {
                pPresenter[0].detachView();
                Assert.fail("Should not succeed.");
            }

            @Override
            public void showAlert(String str) {
                System.out.println("\n\nError detected. Successful.\n\n");
                finished.set(true);
                pPresenter[0].detachView();
            }

            @Override
            public void showAlert(int strId) {
                System.out.println("\n\nError detected. Successful.\n\n");
                finished.set(true);
                pPresenter[0].detachView();
            }
        };

        ObjectGraph objectGraph = ObjectGraph.create(
                new NullModule()
        );
        WelcomePresenter presenter = objectGraph.get(WelcomePresenter.class);
        presenter.attachView(view);
        pPresenter[0] = presenter;

        presenter.ledgerLogin(null);
    }


    @Module(
            complete = false,
            library = true,
            injects = {WelcomePresenter.class}
    )
    class NullModule {

        @Provides
        public RealmConfiguration.Builder provideRealm() {return null;}

        @Provides
        public UserStore provideUserStore() {
            return null;
        }

        @Provides
        public ILedgerService provideLedgerService() {
            return null;
        }

        @Provides
        public ActionChainFactory provideChain() {
            return chainFactory;
        }

        @Provides
        public IFbFactory provideFactory() {
            return null;
        }
    }
}

