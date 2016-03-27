package com.takefive.ledger.view.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.AbsListView;

/**
 * Created by @tourbillon on 3/26/16.
 */
public class ExtendedSwipeRefreshLayout extends SwipeRefreshLayout {

    private AbsListView listView;

    public ExtendedSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ExtendedSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListView(AbsListView alv) {
        listView = alv;
    }

    @Override
    public boolean canChildScrollUp() {
        return listView == null || ViewCompat.canScrollVertically(listView, -1);
    }
}
