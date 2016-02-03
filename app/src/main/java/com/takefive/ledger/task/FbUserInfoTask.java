package com.takefive.ledger.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import javax.inject.Inject;

/**
 * Created by zyu on 2/3/16.
 */
public class FbUserInfoTask extends AsyncTask<AccessToken, Void, FbUserInfo> {

    @Inject
    Bus bus;

    // TODO: inject activity
    @Inject
    Activity mActivity;

    @Override
    protected FbUserInfo doInBackground(AccessToken... params) {
        GraphRequest request = GraphRequest.newMeRequest(params[0], null);
        Bundle config = new Bundle();
        config.putString("fields", "name");
        request.setParameters(config);
        GraphResponse response = request.executeAndWait();
        JSONObject object = response.getJSONObject();

        FbUserInfo info = new FbUserInfo();
        Log.d("debug", object.toString());
        try {
            info.accessToken = params[0];
            info.userName = object.getString("name");
        } catch (JSONException e) {
            Iterator<String> keys = object.keys();
            while(keys.hasNext())
                Log.d("object properties", keys.next());
            e.printStackTrace();
            Snackbar.make(mActivity.findViewById(android.R.id.content), "Failed to contact Facebook.", Snackbar.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final FbUserInfo fbUserInfo) {
        if (fbUserInfo == null)
            return;

        super.onPostExecute(fbUserInfo);
        bus.post(new InfoAvailableEvent<FbUserInfo>() {
            @Override
            public FbUserInfo getUpdate() {
                return fbUserInfo;
            }
        });
    }
}
