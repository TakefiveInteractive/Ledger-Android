package com.takefive.ledger.client;

import android.content.Context;

import com.takefive.ledger.ApplicationContextModule;
import com.takefive.ledger.MainActivity;
import com.takefive.ledger.R;
import com.takefive.ledger.WelcomeActivity;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by @tourbillon on 2/2/16.
 */

@Module(
        includes = ApplicationContextModule.class,
        complete = false,
        library = true,
        injects = {WelcomeActivity.class, MainActivity.class}
)
public class LedgerServiceModule {

    @Provides
    public LedgerService provideLedgerService(Context context) {
        return new Retrofit.Builder()
                .baseUrl("http://127.0.0.1:8080/" + context.getString(R.string.api_version))
                .build()
                .create(LedgerService.class);
    }
}
