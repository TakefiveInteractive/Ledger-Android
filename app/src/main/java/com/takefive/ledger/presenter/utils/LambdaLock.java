package com.takefive.ledger.presenter.utils;

import java.util.concurrent.locks.Lock;

import zyu19.libs.action.chain.config.Producer;

/**
 * Created by zyu on 4/5/16.
 */
public class LambdaLock {

    public static <Out> Out lockAndDo(Lock lock, Producer<Out> runnable) throws Exception {
        Out result = null;
        lock.lock();
        try {
            result = runnable.produce();
        } catch (Exception err) {
            throw err;
        } finally {
            lock.unlock();
        }
        return result;
    }
}

