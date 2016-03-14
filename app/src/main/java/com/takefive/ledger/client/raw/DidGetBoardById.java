package com.takefive.ledger.client.raw;

import java.util.List;

/**
 * Created by zyu on 3/13/16.
 */
public class DidGetBoardById {
    public String _id;
    public String name;
    public boolean isActive;
    public String creator;
    public List<String> members;
    public List<DidGetBillById> bills;
    public String createdAt;
}
