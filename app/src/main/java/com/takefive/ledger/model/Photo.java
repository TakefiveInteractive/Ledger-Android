package com.takefive.ledger.model;

import android.media.Image;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by @tourbillon on 2/1/16.
 */
public class    Photo extends RealmObject {
    public static int TYPE_GENERAL = 0;
    public static int TYPE_AVATAR = 1;

    @PrimaryKey
    private String photoId;
    private int type;

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
