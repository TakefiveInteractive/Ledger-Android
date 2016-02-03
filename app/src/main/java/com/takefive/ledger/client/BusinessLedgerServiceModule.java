package com.takefive.ledger.client;

import android.content.Context;

import com.takefive.ledger.ApplicationContextProvider;
import com.takefive.ledger.R;
import com.takefive.ledger.WelcomeActivity;
import com.takefive.ledger.database.BusinessRealmModule;
import com.takefive.ledger.database.BusinessUserStore;
import com.takefive.ledger.database.BusinessUserStoreModule;
import com.takefive.ledger.database.UserStore;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by @tourbillon on 2/2/16.
 */

@Module(
        complete = false,
        library = true,
        injects = {WelcomeActivity.class}
)
public class BusinessLedgerServiceModule {

    @Provides
    public LedgerService provideLedgerService(Context context,
                                              AuthenticateInterceptor authenticateInterceptor) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authenticateInterceptor)
                .build();
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url) + context.getString(R.string.api_version) + "/")
                .client(client)
                .build()
                .create(LedgerService.class);
    }
}