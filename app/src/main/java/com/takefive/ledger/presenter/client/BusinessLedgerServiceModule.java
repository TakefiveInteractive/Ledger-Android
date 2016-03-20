package com.takefive.ledger.presenter.client;

import android.content.Context;

import com.takefive.ledger.R;
import com.takefive.ledger.view.WelcomeActivity;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LedgerService.class);
    }
}
