package com.takefive.ledger.world;

import java.util.List;
import java.util.Map;

import zyu19.libs.action.chain.config.NiceConsumer;

/**
 * Created by zyu on 3/28/16.
 */
public interface IFbRequest {

    // All calls are blocking (not asynchronous)

    // callback result: {name: "aaa"}
    Map<String, String> getMe() throws Exception;

    // callback result: [ {name: "xxx", id:"0123", avatarUrl:"http://sss"}, {name: "yyy", id:"1234"} ]
    List<Map<String, String>> getMyFriends() throws Exception;
}
