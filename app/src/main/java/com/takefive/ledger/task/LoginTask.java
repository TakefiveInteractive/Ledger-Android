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
import zyu19.libs.action.chain.config.PureAction;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class LoginTask extends AsyncTask<String, Void, String> {

    @Inject
    LedgerService service;

    @Inject
    UserStore userStore;

    @Inject
    Bus bus;

    @Override
    protected String doInBackground(String... strings) {
        try {
            // Login to service
            JSONRequestBody query = new JSONRequestBody(new JSONObject().put("fbToken", strings[0]));
            ResponseBody responseBody = service.login(query).execute().body();
            if(responseBody == null)
                return null;
            return new JSONObject(responseBody.string()).getString("accessToken");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == null) {
            bus.post(new LoginEvent(false, null));
            return;
        }
        super.onPostExecute(s);
        userStore.setAccessToken(s);
        bus.post(new LoginEvent(true, s));
    }
}
