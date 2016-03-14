package com.takefive.ledger.ui;

/**
 * Created by zyu on 3/14/16.
 */
public abstract class MyFragment extends NamedFragment {
    // For communication inside the same Activity. Data should be communicated using Intent
    public abstract void onUpdate();
}
