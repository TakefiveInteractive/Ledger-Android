package com.takefive.ledger.dagger.fb;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.takefive.ledger.dagger.IFbRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zyu on 3/28/16.
 */
public class BusinessFbRequest implements IFbRequest {
    final BusinessFbLoginResult credentials;

    // Cannot use D.I. because this time, we have different object in the same configuration, for different Activities
    public BusinessFbRequest(BusinessFbLoginResult credentials) {
        this.credentials = credentials;
    }

    @Override
    public Map<String, String> getMe() throws Exception {
        GraphRequest request = GraphRequest.newMeRequest(credentials.token, null);
        Bundle config = new Bundle();
        config.putString("fields", "name");
        request.setParameters(config);
        GraphResponse response = request.executeAndWait();
        JSONObject object = response.getJSONObject();

        HashMap<String, String> result = new HashMap<>();
        result.put("name", object.getString("name"));
        return result;
    }

    @Override
    public List<Map<String, String>> getMyFriends() throws Exception {
        GraphRequest request =
                GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), null);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");
        request.setParameters(parameters);
        JSONArray response = request.executeAndWait().getJSONObject().getJSONArray("data");

        List<Map<String, String>> infoList = new ArrayList<>();
        for (int i = 0; i < response.length(); ++i) {
            JSONObject user = response.getJSONObject(i);
            HashMap<String, String> info = new HashMap<>();
            info.put("id", user.getString("id"));
            info.put("name", user.getString("name"));
            info.put("avatarUrl", user.getJSONObject("picture").getJSONObject("data").getString("url"));
            infoList.add(info);
        }
        return infoList;
    }
}
