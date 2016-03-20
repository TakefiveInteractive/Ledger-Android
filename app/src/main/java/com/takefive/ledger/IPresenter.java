package com.takefive.ledger;

/**
 * Created by zyu on 3/19/16.
 *
 * @param V the IView associated with this presenter
 */
public interface IPresenter <V extends IView> {
    void attachView(V view);
    void detachView();
}
