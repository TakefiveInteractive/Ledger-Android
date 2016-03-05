package com.takefive.ledger.task;

import android.os.AsyncTask;

import com.squareup.otto.Bus;
import com.takefive.ledger.client.JSONRequestBody;
import com.takefive.ledger.client.LedgerService;
import com.takefive.ledger.database.UserStore;
import com.takefive.ledger.model.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;
import okhttp3.ResponseBody;
import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.config.NiceConsumer;
import zyu19.libs.action.chain.config.PureAction;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class LoginTask implements NiceConsumer<ActionChain> {

    @Inject
    LedgerService service;

    @Inject
    UserStore userStore;

    @Override
    public void consume(ActionChain chain) {
        chain.netThen((String fbToken) -> {
            JSONRequestBody query = new JSONRequestBody(new JSONObject().put("fbToken", fbToken));
            ResponseBody responseBody = service.login(query).execute().body();
            if(responseBody == null)
                throw new IOException();

            String ans = new JSONObject(responseBody.string()).getString("accessToken");
            userStore.setAccessToken(ans);
            return ans;
        });
    }
}
