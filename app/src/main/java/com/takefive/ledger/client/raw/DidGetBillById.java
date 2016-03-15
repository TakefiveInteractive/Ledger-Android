package com.takefive.ledger.client.raw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

/**
 * Created by zyu on 3/13/16.
 */
public class DidGetBillById {
    public String _id;
    public String title;
    public String description;
    public String time;
    public List<String> photoUrls;
    public String recipient;
    public Double latitude;
    public Double longitude;
    public List<DidGetAmount> amounts;
    public boolean isPaid;
    public String creator;
    public boolean isDeleted;

    public Date getTime() throws ParseException {
        SimpleDateFormat dater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        return dater.parse(time);
    }

    public double getTotalAmount() {
        double sum = 0;
        for(DidGetAmount amount : amounts) {
            sum += amount.balance;
        }
        return sum;
    }
}
