package com.takefive.ledger.ui;

import android.content.res.Resources;
import android.support.v4.app.Fragment;

/**
 * Created by zyu on 2/3/16.
 */
public class NamedFragment extends Fragment {
    private String mTitle = "No name";

    public NamedFragment setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }
}
