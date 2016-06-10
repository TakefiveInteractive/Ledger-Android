package com.takefive.ledger.view;

import java.util.*;

import com.takefive.ledger.midData.ledger.RawMyBoards;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.IView;
import com.takefive.ledger.midData.view.ShownBill;
import com.takefive.ledger.model.Board;
import com.takefive.ledger.model.Person;

import io.realm.RealmList;

/**
 * Created by zyu on 3/19/16.
 */
public interface IMainView extends IView {
    // show xxx clears the previous content of xxx and show the new one passed in
    // TODO: convert all RAW types to SHOWN types
    void showBillsList(List<ShownBill> bills);
    void showMyBoards(RealmList<Board> boards);
    void showMyUserInfo(Person me);
    void startRefreshing();
    void stopRefreshing();
    void setCurrentBoardId(String id);
    void setBoardTitle(String boardName);

    /**
     * View is responsible to log out of Facebook in this callback.
     */
    void finishLogout();
}
