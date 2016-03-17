package com.takefive.ledger.task;

import com.takefive.ledger.client.JSONRequestBody;
import com.takefive.ledger.client.LedgerService;
import com.takefive.ledger.database.UserStore;

import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.config.NiceConsumer;

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
