package com.takefive.ledger.presenter.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by zyu on 4/5/16.
 */
public class RxLambdaLock {

    public static <Out> Observable<Out> lockAndDo(Lock lock, Callable<Out> runnable) {
        return Observable.create(subscriber -> {
            Out result = null;
            lock.lock();
            try {
                result = runnable.call();
                if(subscriber != null && !subscriber.isUnsubscribed()) {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }
            } catch (Exception err) {
                if(subscriber != null && !subscriber.isUnsubscribed())
                    subscriber.onError(err);
            } finally {
                lock.unlock();
            }
        });
    }

    public static <Out> Observable<Out> lockAndDo(Lock lock, Func0<Observable<Out>> runnable) {
        return Observable.create(subscriber -> {
            lock.lock();
            if(!subscriber.isUnsubscribed()) {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        }).flatMap(obj -> runnable.call()
        ).doOnCompleted(() -> lock.unlock()
        ).doOnError(err -> lock.unlock());
    }
}

