package com.takefive.ledger.presenter.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by @tourbillon on 2/4/16.
 */
public class DateTimeConverter {

    private static DateFormat[] formats = {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    };

    public static Date fromISOString(String string) throws TimeException {
        for (DateFormat format : formats) {
            try {
                return format.parse(string);
            } catch (Exception e) {}
        }

        throw new TimeException("The string cannot be parsed.");
    }
}
