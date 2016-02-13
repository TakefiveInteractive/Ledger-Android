package com.takefive.ledger;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import zyu19.libs.action.chain.config.ThreadChanger;
import zyu19.libs.action.chain.config.ThreadPolicy;

/**
 * Created by zyu on 1/30/16.
 */
public class Helpers {

    // This method does not rely on any injection, thus static.
    public static void setMargins(View v, Integer t, Integer b, Integer l, Integer r) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            if (l != null)
                p.leftMargin = l;
            if (t != null)
                p.topMargin = t;
            if (r != null)
                p.rightMargin = r;
            if (b != null)
                p.bottomMargin = b;
            v.requestLayout();
        }
    }

    public static String shortDate(int LongMedShort, Date d) {
        Locale locale = Locale.getDefault();
        Date currDate = new Date();

        if (currDate.getTime() - d.getTime() < TimeUnit.DAYS.toMillis(1)) {
            return DateFormat.getTimeInstance(LongMedShort, locale).format(d);
        } else {
            return DateFormat.getDateInstance(LongMedShort, locale).format(d);
        }
    }

    public static String longDate(int LongMedShort, Date d) {
        Locale locale = Locale.getDefault();
        return DateFormat.getTimeInstance(LongMedShort, locale).format(d) +
                "\n" + DateFormat.getDateInstance(LongMedShort, locale).format(d);
    }

    public static ThreadPolicy getThreadPolicy(Activity activity, ExecutorService multiThreadConfig) {
        return new ThreadPolicy(runnable -> activity.runOnUiThread(runnable), multiThreadConfig);
    }
}
