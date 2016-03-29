package com.takefive.ledger.mid_data.ledger;

import java.util.List;

/**
 * Created by zyu on 3/13/16.
 */
public class RawBoard {
    public String _id;
    public String name;
    public boolean isActive;
    public String creator;
    public List<String> members;
    public List<RawBill> bills;
    public String createdAt;
}
