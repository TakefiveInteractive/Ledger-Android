package com.takefive.ledger.world;

/**
 * Created by zyu on 3/28/16.
 */
public interface IFbFactory {
    IFbRequest newRequest(IFbLoginResult loginResult);
}
