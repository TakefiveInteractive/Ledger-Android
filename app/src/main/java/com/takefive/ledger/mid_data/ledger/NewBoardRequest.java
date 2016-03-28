package com.takefive.ledger.mid_data.ledger;

import java.util.List;

/**
 * Created by @tourbillon on 3/23/16.
 */
public class NewBoardRequest {
    /* TODO: in principal, IView does not touch any Models */
    public String name;
    public boolean isActive;
    public List<String> members;
    public List<String> members_fromfb = null;
    public String createdAt = null;
}
