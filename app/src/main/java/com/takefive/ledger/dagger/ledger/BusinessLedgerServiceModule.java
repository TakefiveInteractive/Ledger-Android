package com.takefive.ledger.dagger.ledger;

import android.content.Context;

import com.takefive.ledger.R;
import com.takefive.ledger.view.WelcomeActivity;
import com.takefive.ledger.dagger.ILedgerService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    @Singleton
    public ILedgerService provideLedgerService(Context context,
                                              AuthenticateInterceptor authenticateInterceptor) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authenticateInterceptor)
                .addInterceptor(logging)
                .cache(new Cache(context.getCacheDir(), 10 * 1024 * 1024)) // 10MB
                .build();
        return new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url) + context.getString(R.string.api_version) + "/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ILedgerService.class);
    }
}
