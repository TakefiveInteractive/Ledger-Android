package com.takefive.ledger.midData.ledger;

import java.util.List;

/**
 * Created by zyu on 3/17/16.
 */
public class RawPerson {
    public String _id;

    // ILedgerService does not provide this field
    public String name;
    public String avatarUrl;
    public String facebookId;
    public List<String> bills;
    public List<String> boards;
    public String createdAt;
    public String lastLoginAt;
}
