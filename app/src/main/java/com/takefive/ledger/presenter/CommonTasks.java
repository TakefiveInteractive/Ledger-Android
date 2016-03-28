package com.takefive.ledger.presenter;

import com.takefive.ledger.model.RawBoard;
import com.takefive.ledger.model.RawMyBoards;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.model.db.Board;
import com.takefive.ledger.model.db.Entry;
import com.takefive.ledger.model.db.MyBoards;
import com.takefive.ledger.model.db.Person;
import com.takefive.ledger.dagger.IFbRequest;
import com.takefive.ledger.dagger.ILedgerService;
import com.takefive.ledger.dagger.ledger.JSONRequestBody;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.presenter.utils.DateTimeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Response;
import zyu19.libs.action.chain.ReadOnlyChain;

/**
 * Created by zyu on 3/19/16.
 */
public class CommonTasks {

    @Inject
    ILedgerService service;

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

    FbUserInfo getMyFbUserInfo(IFbRequest request) throws Exception {
        FbUserInfo info = new FbUserInfo();
        try {
            Map<String, String> ans = request.getMe();
            info.userName = ans.get("name");
        } catch (JSONException e) {
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
            target.setName(person.name);
            target.setFacebookId(person.facebookId);
            target.setAvatarUrl(person.avatarUrl);
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

    ReadOnlyChain syncBoardInfo(RawBoard board) {
        return realmAccess.process(realm -> {
            Board target = new Board();
            target.setActive(board.isActive);
            target.setCreator(board.creator);
            target.setBoardId(board._id);
            target.setName(board.name);
            target.setCreatedAt(DateTimeConverter.fromISOString(board.createdAt).getTime());

            realm.beginTransaction();

            Board result = realm.where(Board.class)
                    .equalTo("boardId", board._id)
                    .findFirst();
            if (result != null)
                result.removeFromRealm();
            realm.copyToRealm(target);

            realm.commitTransaction();

            return target;
        });
    }

    ReadOnlyChain syncMyBoardsInfo(RawMyBoards myBoards) {
        return realmAccess.process(realm -> {

            MyBoards target = new MyBoards();
            RealmList<Entry> entries = new RealmList<>();
            for (RawMyBoards.Entry entry : myBoards.boards) {
                entries.add(new Entry(entry.id, entry.name));
            }
            target.setBoards(entries);

            realm.beginTransaction();

            RealmResults<MyBoards> result = realm.where(MyBoards.class).findAll();
            result.clear();
            realm.copyToRealm(target);

            realm.commitTransaction();

            return target;
        });
    }

}
