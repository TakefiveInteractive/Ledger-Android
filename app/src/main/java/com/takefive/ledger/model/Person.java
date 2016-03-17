package com.takefive.ledger.model;

import java.util.Date;
import java.util.List;

/**
 * Created by zyu on 3/17/16.
 */
public class Person {
    private String _id;

    // LedgerService does not provide this field
    private String name;
    private String avatarUrl;
    private String facebookId;
    private List<String> bills;
    private List<String> boards;
    private String createdAt;
    private String lastLoginAt;
}
