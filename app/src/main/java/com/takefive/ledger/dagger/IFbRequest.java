package com.takefive.ledger.dagger;

import com.takefive.ledger.midData.fb.FbUserInfo;

import java.util.List;

/**
 * Created by zyu on 3/28/16.
 */
public interface IFbRequest {

    // All calls are blocking (not asynchronous)

    // callback result: {name: "aaa"}
    FbUserInfo getMe() throws Exception;

    // callback result: [ {name: "xxx", id:"0123", avatarUrl:"http://sss"}, {name: "yyy", id:"1234"} ]
    List<FbUserInfo> getMyFriends() throws Exception;

    void logout() throws Exception;
}
