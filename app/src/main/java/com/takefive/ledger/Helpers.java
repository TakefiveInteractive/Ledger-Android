package com.takefive.ledger;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zyu on 1/30/16.
 */
public class Helpers {
    public static void setMargins(View v, Integer t, Integer b, Integer l, Integer r) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            if(l != null)
                p.leftMargin = l;
            if(t != null)
                p.topMargin = t;
            if(r != null)
                p.rightMargin = r;
            if(b != null)
                p.bottomMargin = b;
            v.requestLayout();
        }
    }
}
