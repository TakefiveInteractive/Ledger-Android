package com.takefive.ledger.view.utils;

import android.app.Activity;
import android.os.Bundle;

import com.takefive.ledger.R;

public class TestViews extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_bill_list);
    }
}
