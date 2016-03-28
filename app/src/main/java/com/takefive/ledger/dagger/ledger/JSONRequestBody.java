package com.takefive.ledger.dagger.ledger;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class JSONRequestBody extends RequestBody {

    private JSONObject jsonObject;

    public JSONRequestBody(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json; charset=utf-8");
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        MediaType contentType = contentType();
        Charset charset = contentType.charset();
        byte[] bytes = jsonObject.toString().getBytes(charset);
        bufferedSink.write(bytes, 0, bytes.length);
    }
}
