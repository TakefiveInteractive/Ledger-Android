package com.takefive.ledger;

/**
 * Created by zyu on 3/19/16.
 */
public interface IView {
    /*
    Currently no common behavior here.
    Originally a "getContext" was required but we used D.I. for that
     */

    void showAlert(String str);
    void showAlert(int strId);
}
