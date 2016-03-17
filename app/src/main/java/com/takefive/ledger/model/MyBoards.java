package com.takefive.ledger.model;

import java.util.List;

/**
 * Created by zyu on 3/13/16.
 */
public class MyBoards {
    public static class Entry {
        Entry(String a, String b) {
            id = a;
            name = b;
        }
        public String id;
        public String name;
    }

    public List<Entry> boards;
}
