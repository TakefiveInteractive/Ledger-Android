package com.takefive.ledger.client;

import com.takefive.ledger.model.MyBoards;
import com.takefive.ledger.model.Board;

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
    Call<MyBoards> getMyBoards();

    @GET("board/{id}")
    Call<Board> getBoardById(@Path("id") String id);

}
