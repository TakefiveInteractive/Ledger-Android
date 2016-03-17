package com.takefive.ledger.model;

import java.util.List;

/**
 * Created by zyu on 3/13/16.
 */
public class Board {
    public String _id;
    public String name;
    public boolean isActive;
    public String creator;
    public List<String> members;
    public List<Bill> bills;
    public String createdAt;
}
