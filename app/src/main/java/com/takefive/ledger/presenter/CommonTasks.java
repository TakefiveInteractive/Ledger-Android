package com.takefive.ledger.presenter;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.model.db.Person;
import com.takefive.ledger.presenter.client.JSONRequestBody;
import com.takefive.ledger.presenter.client.LedgerService;
import com.takefive.ledger.presenter.database.RealmAccess;
import com.takefive.ledger.presenter.database.UserStore;
import com.takefive.ledger.presenter.utils.DateTimeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import zyu19.libs.action.chain.ReadOnlyChain;

/**
 * Created by zyu on 3/19/16.
 */
public class CommonTasks {

    @Inject
    LedgerService service;

    @Inject
    UserStore userStore;

    @Inject
    RealmAccess realmAccess;

    String getAndSaveAccessToken(String fbToken) throws IOException, JSONException {
        JSONRequestBody query = new JSONRequestBody(new JSONObject().put("fbToken", fbToken));
        ResponseBody responseBody = service.login(query).execute().body();
        if(responseBody == null)
            throw new IOException();

        String ans = new JSONObject(responseBody.string()).getString("accessToken");
        userStore.setAccessToken(ans);
        return ans;
    }

    FbUserInfo getFbUserInfo(AccessToken fbToken) throws Exception {
        GraphRequest request = GraphRequest.newMeRequest(fbToken, null);
        Bundle config = new Bundle();
        config.putString("fields", "name");
        request.setParameters(config);
        GraphResponse response = request.executeAndWait();
        JSONObject object = response.getJSONObject();

        FbUserInfo info = new FbUserInfo();
        try {
            info.accessToken = fbToken;
            info.userName = object.getString("name");
        } catch (JSONException e) {
            Iterator<String> keys = object.keys();
            while(keys.hasNext())
                Log.d("object properties", keys.next());
            e.printStackTrace();
            throw e;
        }
        return info;
    }

    RawPerson getAndSyncMyUserInfo() throws Exception {
        // TODO: let Retrofit cache for us
        Response<RawPerson> me = service.getCurrentPerson().execute();
        if(!me.isSuccessful())
            throw new IOException(me.errorBody().string());
        userStore.setUserId(me.body()._id);
        syncUserInfo(me.body());
        return me.body();
    }

    ReadOnlyChain syncUserInfo(RawPerson person) {
        return realmAccess.process(realm -> {
            Person target = new Person();
            target.setPersonId(person._id);
            target.setCreatedAt(DateTimeConverter.fromISOString(person.createdAt).getTime());

            // TODO: add etags to avoid unnecessary writes?

            realm.beginTransaction();

            // TODO: synchronize bills' and boards' references in DB.

            Person result = realm.where(Person.class)
                    .equalTo("personId", target.getPersonId())
                    .findFirst();
            if (result != null)
                result.removeFromRealm();
            realm.copyToRealm(target);

            realm.commitTransaction();
            return target;
        });
    }

}
