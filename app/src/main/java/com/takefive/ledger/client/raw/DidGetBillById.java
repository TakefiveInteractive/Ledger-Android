package com.takefive.ledger.client.raw;

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
}
