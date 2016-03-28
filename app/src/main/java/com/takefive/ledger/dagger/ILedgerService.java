package com.takefive.ledger.dagger;

import com.takefive.ledger.mid_data.ledger.RawMyBoards;
import com.takefive.ledger.mid_data.ledger.RawBoard;
import com.takefive.ledger.mid_data.ledger.RawPerson;
import com.takefive.ledger.mid_data.ledger.NewBoardRequest;

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
public interface ILedgerService {

    @POST("auth/login")
    Call<ResponseBody> login(@Body RequestBody body);

    @GET("person")
    Call<RawPerson> getCurrentPerson();

    @GET("person/{id}")
    Call<RawPerson> getPerson(@Path("id") String id);

    @GET("board")
    Call<RawMyBoards> getMyBoards();

    @POST("board")
    Call<ResponseBody> createBoard(@Body NewBoardRequest body);

    @GET("board/{id}")
    Call<RawBoard> getBoardById(@Path("id") String id);

}
