package com.takefive.ledger.task;

/**
 * Created by zyu on 2/3/16.
 */
// This is a event indicating that a task failed.
public class TaskFailEvent<T> {
    final Exception mCause;
    final T mTask;
    public TaskFailEvent(Exception cause, T task) {
        mCause = cause;
        mTask = task;
    }

    T getOriginalTask() {
        return mTask;
    }

    Exception getCause() {
        return mCause;
    }
}
