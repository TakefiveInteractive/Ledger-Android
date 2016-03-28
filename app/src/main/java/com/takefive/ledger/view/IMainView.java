package com.takefive.ledger.view;

import java.util.*;

import com.takefive.ledger.mid_data.ledger.RawBill;
import com.takefive.ledger.mid_data.ledger.RawMyBoards;
import com.takefive.ledger.mid_data.ledger.RawPerson;
import com.takefive.ledger.IView;

/**
 * Created by zyu on 3/19/16.
 */
public interface IMainView extends IView {
    // show xxx clears the previous content of xxx and show the new one passed in
    void showBillsList(List<RawBill> bills);
    void showMyBoards(RawMyBoards boards);
    void showMyUserInfo(RawPerson me);
    void stopRefreshing();
    void setCurrentBoardId(String id);
    void setBoardTitle(String boardName);
}
