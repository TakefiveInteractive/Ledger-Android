package com.takefive.ledger.world.ledger;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class Helper {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static RequestBody jsonToRequestBody(JSONObject o) {
        return RequestBody.create(JSON, o.toString());
    }
}
