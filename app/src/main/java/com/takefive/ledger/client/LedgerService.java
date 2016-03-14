package com.takefive.ledger.client;

import com.takefive.ledger.client.raw.DidGetBoard;
import com.takefive.ledger.client.raw.DidGetBoardById;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by @tourbillon on 2/2/16.
 */
public interface LedgerService {

    @POST("auth/login")
    Call<ResponseBody> login(@Body RequestBody body);

    @GET("person")
    Call<ResponseBody> getCurrentPerson();

    @GET("board")
    Call<DidGetBoard> getMyBoards();

    @GET("board/{id}")
    Call<DidGetBoardById> getBoardById(@Path("id") String id);

}
