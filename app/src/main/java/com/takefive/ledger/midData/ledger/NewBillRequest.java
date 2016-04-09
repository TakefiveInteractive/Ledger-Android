package com.takefive.ledger.midData.ledger;

import java.util.List;

/**
 * Created by @tourbillon on 4/6/16.
 */
public class NewBillRequest {
    public String boardId;
    public String title;
    public String description;
    public List<PersonAmountPair> amounts;
}
