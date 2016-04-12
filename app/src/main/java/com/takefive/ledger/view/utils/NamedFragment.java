package com.takefive.ledger.view.utils;

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
