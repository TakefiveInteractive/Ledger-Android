package com.takefive.ledger.task;

/**
 * Created by @tourbillon on 2/2/16.
 */
public interface InfoUpdatedEvent<T> {
    T getUpdate();
}
