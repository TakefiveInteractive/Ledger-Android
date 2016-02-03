package com.takefive.ledger.task;

import android.os.AsyncTask;

import com.squareup.otto.Bus;
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
import retrofit2.Response;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class UpdateUserInfoTask extends AsyncTask<String, Void, Person> {

    @Inject
    UserStore userStore;

    @Inject
    Bus bus;

    @Inject
    Realm realm;

    @Inject
    LedgerService service;

    @Override
    protected Person doInBackground(String... strings) {
        try {
            Response<ResponseBody> response = service.getCurrentPerson().execute();
            if (response.code() != 200)
                return null;

            ResponseBody responseBody = response.body();
            JSONObject jsonObject = new JSONObject(responseBody.string());

            String ourUserID = jsonObject.getString("_id");

            // Set user ID in preferences
            userStore.setUserId(ourUserID);

            // Set user details in database
            realm.beginTransaction();
            Person result = realm.where(Person.class)
                    .equalTo("personId", ourUserID)
                    .findFirst();
            if (result == null)
                result = realm.createObject(Person.class);
            result.setPersonId(ourUserID);
            result.setName(strings[0]);
            result.setFacebookId(jsonObject.getString("facebookId"));
            result.setCreatedAt(new Date(Integer.parseInt(jsonObject.getString("createdAt").toString()) * 1000));
            realm.commitTransaction();
            return result;
        } catch (IOException e) {
            if (realm.isInTransaction())
                realm.cancelTransaction();
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            if (realm.isInTransaction())
                realm.cancelTransaction();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Person person) {
        if (person == null)
            return;

        super.onPostExecute(person);
        bus.post(new UserInfoUpdatedEvent(person));
    }

}
