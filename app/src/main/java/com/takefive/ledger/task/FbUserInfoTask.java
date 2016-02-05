package com.takefive.ledger.task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import javax.inject.Inject;

import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.config.ChainEditor;

/**
 * Created by zyu on 2/3/16.
 */
public class FbUserInfoTask implements ChainEditor {


    @Override
    public void edit(ActionChain chain) {
        chain.netThen((AccessToken token) -> {
            GraphRequest request = GraphRequest.newMeRequest(token, null);
            Bundle config = new Bundle();
            config.putString("fields", "name");
            request.setParameters(config);
            GraphResponse response = request.executeAndWait();
            JSONObject object = response.getJSONObject();

            FbUserInfo info = new FbUserInfo();
            try {
                info.accessToken = token;
                info.userName = object.getString("name");
            } catch (JSONException e) {
                Iterator<String> keys = object.keys();
                while(keys.hasNext())
                    Log.d("object properties", keys.next());
                e.printStackTrace();
                throw e;
            }
            return info;
        });
    }
}
