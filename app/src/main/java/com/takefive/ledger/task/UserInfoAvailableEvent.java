package com.takefive.ledger.task;

import com.takefive.ledger.model.Person;

/**
 * Created by @tourbillon on 2/2/16.
 */
public class UserInfoAvailableEvent implements InfoAvailableEvent<Person> {

    private Person person;

    public UserInfoAvailableEvent(Person person) {
        this.person = person;
    }

    @Override
    public Person getUpdate() {
        return person;
    }
}
