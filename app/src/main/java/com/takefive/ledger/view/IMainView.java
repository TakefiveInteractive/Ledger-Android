package com.takefive.ledger.view;

import java.util.*;
import com.takefive.ledger.model.*;
import com.takefive.ledger.IView;
import com.takefive.ledger.model.db.Person;
import com.takefive.ledger.presenter.FbUserInfo;

/**
 * Created by zyu on 3/19/16.
 */
public interface IMainView extends IView {
    // show xxx clears the previous content of xxx and show the new one passed in
    void showBillsList(List<RawBill> bills);
    void showMyBoards(RawMyBoards boards);
    void showMyUserInfo(RawPerson me);
    void stopRefreshing();
}
