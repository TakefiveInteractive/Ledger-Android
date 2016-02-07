package com.takefive.ledger.task;

import android.util.Log;

import com.squareup.otto.Bus;
import com.takefive.ledger.client.LedgerService;
import com.takefive.ledger.database.UserStore;
import com.takefive.ledger.model.Person;
import com.takefive.ledger.model.Photo;
import com.takefive.ledger.util.DateTimeConverter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;
import zyu19.libs.action.chain.ActionChain;
import zyu19.libs.action.chain.config.ChainEditor;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class UpdateUserInfoTask implements ChainEditor {

    @Inject
    UserStore userStore;

    @Inject
    Realm realm;

    @Inject
    LedgerService service;

    @Override
    public void edit(ActionChain chain) {
        chain.netThen((String userName) -> {
            Response<ResponseBody> response = service.getCurrentPerson().execute();
            Person person = new Person();
            if (response.code() != 200)
                throw new IOException();
            ResponseBody responseBody = response.body();
            JSONObject jsonObject = new JSONObject(responseBody.string());

            String ourUserID = jsonObject.getString("_id");

            // Set user ID in preferences
            userStore.setUserId(ourUserID);

            Photo photo = new Photo();
            photo.setPhotoUrl(jsonObject.getString("avatarUrl"));
            photo.setType(Photo.TYPE_AVATAR);
            person.setName(userName);
            person.setAvatar(photo);
            person.setFacebookId(jsonObject.getString("facebookId"));
            person.setCreatedAt(DateTimeConverter.toDate(jsonObject.getString("createdAt")));
            person.setPersonId(ourUserID);

            Log.d("UpUserInfo", "A"+person);

            return person;
        }).uiThen((Person newPerson) -> {
            try {
                Log.d("UpUserInfo", newPerson == null ? "null" : newPerson.toString());
                // Set user details in database
                realm.beginTransaction();
                Person result = realm.where(Person.class)
                        .equalTo("personId", newPerson.getPersonId())
                        .findFirst();
                if (result != null)
                    result.removeFromRealm();
                realm.copyToRealm(newPerson);
                realm.commitTransaction();
                // MAYBE NOT NEEDED: bus.post(new UserInfoUpdatedEvent(result));
                return newPerson;
            } catch(Exception err) {
                // Maybe errorHolder.retry() ?

                if (realm.isInTransaction())
                    realm.cancelTransaction();
                err.printStackTrace();
                throw err;
            }
        });
    }
}
