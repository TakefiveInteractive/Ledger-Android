package com.takefive.ledger.view.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by @tourbillon on 4/8/16.
 */
public class ControllableViewPager extends ViewPager {

    private boolean swipeEnabled;

    public ControllableViewPager(Context context) {
        super(context);
        init();
    }

    public ControllableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        swipeEnabled = true;
    }

    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (swipeEnabled)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (swipeEnabled)
            return super.onTouchEvent(ev);
        else
            return false;
    }

}
