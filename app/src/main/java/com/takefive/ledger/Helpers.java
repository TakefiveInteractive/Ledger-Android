package com.takefive.ledger;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

    public static final NumberFormat CURRENCY_FORMAT;
    static {
        CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
        CURRENCY_FORMAT.setRoundingMode(RoundingMode.DOWN);
    }

    public static String parseText(String s) {
        /* Old implementation
        int dotIndex = current.indexOf('.');
        if (dotIndex != -1 && dotIndex < current.length() - 1) {
            String integer = current.substring(0, dotIndex);
            String decimal = current.substring(dotIndex + 1).replaceAll("[^\\d]", "");
            if (decimal.length() >= 2)
                decimal = decimal.substring(0, 2);
            else
                decimal += "0";
            current = integer + "." + decimal;
        } else
            current += ".00";
        return current;
        */

        String current = s.replaceAll("[^\\d.]", "");
        return parseText(Double.parseDouble(current));
    }

    public static String parseText(double amount) {
        /* Old implementation
        return parseText(Double.toString(amount));
        */

        return CURRENCY_FORMAT.format(amount);
    }

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

    public static int getStatusBarHeight(Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
