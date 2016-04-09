package com.takefive.ledger.dagger;

import com.takefive.ledger.midData.ledger.NewBillRequest;
import com.takefive.ledger.midData.ledger.RawBoardSimple;
import com.takefive.ledger.midData.ledger.RawMyBoards;
import com.takefive.ledger.midData.ledger.RawBoard;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.midData.ledger.NewBoardRequest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    // getMyBoards returns really little information. But make sure ETag is not included in request.
    @GET("board?unsafePopulate=true")
    Call<RawMyBoards> getMyBoards();

    @POST("board")
    Call<ResponseBody> createBoard(@Body NewBoardRequest body);

    @GET("board/{id}?unsafePopulate=true")
    Call<RawBoard> getBoardByIdInflated(@Path("id") String id);

    @GET("board/{id}?unsafePopulate=false")
    Call<RawBoardSimple> getBoardByIdSimple(@Path("id") String id);

    @POST("bill")
    Call<ResponseBody> createBill(@Body NewBillRequest body);
}
