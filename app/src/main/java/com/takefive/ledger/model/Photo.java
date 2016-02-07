package com.takefive.ledger.model;

import android.media.Image;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class    Photo extends RealmObject {

    @Ignore
    public static int TYPE_GENERAL = 0;
    @Ignore
    public static int TYPE_AVATAR = 1;

    private String photoUrl;
    private int type;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
