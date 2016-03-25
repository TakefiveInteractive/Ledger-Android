package com.takefive.ledger.model.request;

import java.util.List;

/**
 * Created by @tourbillon on 3/23/16.
 */
public class NewBoardRequest {
    public String name;
    public boolean isActive;
    public List<String> members;
    public List<String> members_fromfb = null;
    public String createdAt = null;
}
