package com.takefive.ledger.client;

import android.content.Context;

import com.takefive.ledger.database.UserStore;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class AuthenticateInterceptor implements Interceptor {

    private UserStore userStore;

    @Inject
    public AuthenticateInterceptor(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!request.url().toString().contains("/login")) {
            request = request.newBuilder()
                    .addHeader("bb-token", userStore.getMostRecentAccessToken())
                    .build();
        }
        return chain.proceed(request);
    }
}
