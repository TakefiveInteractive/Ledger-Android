package com.takefive.ledger.view.utils;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import zyu19.libs.action.chain.config.NiceConsumer;

/**
 * Created by zyu on 4/12/16.
 */
public class CustomTabLayout extends TabLayout {

    final List<NiceConsumer<Tab>> onTabReselectedListeners = new ArrayList<>();

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addOnTabReselectedListener(NiceConsumer<Tab> consumer) {
        onTabReselectedListeners.add(consumer);
    }

    public void removeOnTabReselectedListener(NiceConsumer<Tab> consumer) {
        onTabReselectedListeners.remove(consumer);
    }

    public void clearOnTabReselectedListener() {
        onTabReselectedListeners.clear();
    }

    @Override
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        super.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                onTabSelectedListener.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(Tab tab) {
                onTabSelectedListener.onTabUnselected(tab);
            }

            @Override
            public void onTabReselected(Tab tab) {
                onTabSelectedListener.onTabReselected(tab);
                for(NiceConsumer<Tab> listener : onTabReselectedListeners) {
                    listener.consume(tab);
                }
            }
        });
    }
}
