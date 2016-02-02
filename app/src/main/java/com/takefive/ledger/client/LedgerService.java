package com.takefive.ledger.client;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by @tourbillon on 2/2/16.
 */
public interface LedgerService {

    @POST("login")
    Call<ResponseBody> login(@Body String body);
}
