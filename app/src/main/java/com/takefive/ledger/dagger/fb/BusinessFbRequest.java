package com.takefive.ledger.dagger.fb;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.takefive.ledger.dagger.IFbRequest;
import com.takefive.ledger.midData.fb.FbUserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public FbUserInfo getMe() throws Exception {
        GraphRequest request = GraphRequest.newMeRequest(credentials.token, null);
        Bundle config = new Bundle();
        config.putString("fields", "name");
        request.setParameters(config);
        GraphResponse response = request.executeAndWait();
        if(response.getError() != null)
            throw new IOException(response.getError().getErrorMessage());
        JSONObject object = response.getJSONObject();

        FbUserInfo userInfo = new FbUserInfo();
        userInfo.name = object.getString("name");
        return userInfo;
    }

    @Override
    public List<FbUserInfo> getMyFriends() throws Exception {
        GraphRequest request =
                GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), null);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");
        request.setParameters(parameters);
        JSONArray response = request.executeAndWait().getJSONObject().getJSONArray("data");

        List<FbUserInfo> infoList = new ArrayList<>();
        for (int i = 0; i < response.length(); ++i) {
            JSONObject user = response.getJSONObject(i);
            FbUserInfo info = new FbUserInfo();
            info.id = user.getString("id");
            info.name = user.getString("name");
            info.avatarUrl = user.getJSONObject("picture").getJSONObject("data").getString("url");
            infoList.add(info);
        }
        return infoList;
    }

    @Override
    public void logout() throws Exception {
        new GraphRequest(credentials.token, "/me/permissions/", null, HttpMethod.DELETE)
                .executeAndWait();
        LoginManager.getInstance().logOut();
    }
}
