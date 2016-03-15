package com.takefive.ledger.client.raw;

import java.util.List;

/**
 * Created by zyu on 3/13/16.
 */
public class DidGetBoard {
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
