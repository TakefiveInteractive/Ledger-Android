package com.takefive.ledger.view;

import java.util.*;

import com.takefive.ledger.midData.ledger.RawMyBoards;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.IView;
import com.takefive.ledger.midData.view.ShownBill;

/**
 * Created by zyu on 3/19/16.
 */
public interface IMainView extends IView {
    // show xxx clears the previous content of xxx and show the new one passed in
    // TODO: convert all RAW types to SHOWN types
    void showBillsList(List<ShownBill> bills);
    void showMyBoards(RawMyBoards boards);
    void showMyUserInfo(RawPerson me);
    void stopRefreshing();
    void setCurrentBoardId(String id);
    void setBoardTitle(String boardName);

    /**
     * View is responsible to log out of Facebook in this callback.
     */
    void finishLogout();
}
