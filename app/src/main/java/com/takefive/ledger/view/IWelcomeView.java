package com.takefive.ledger.view;

import com.takefive.ledger.IView;

/**
 * Created by zyu on 3/19/16.
 */
public interface IWelcomeView extends IView {
    void onLoginSuccess(String username);
}
