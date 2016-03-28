package com.takefive.ledger.world.fb;

import com.takefive.ledger.view.MainActivity;
import com.takefive.ledger.view.WelcomeActivity;
import com.takefive.ledger.world.IFbFactory;
import com.takefive.ledger.world.IFbLoginResult;
import com.takefive.ledger.world.IFbRequest;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zyu on 3/28/16.
 */
@Module(
        complete = false,
        library = true,
        injects = {WelcomeActivity.class, MainActivity.class}
)
public class BusinessFbFactoryProvider {
    @Provides
    public IFbFactory provideFbFactory() {
        return new IFbFactory() {
            @Override
            public IFbRequest newRequest(IFbLoginResult loginResult) {
                return new BusinessFbRequest((BusinessFbLoginResult)loginResult);
            }
        };
    }
}
