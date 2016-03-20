package com.takefive.ledger;

import com.takefive.ledger.model.RawBill;
import com.takefive.ledger.model.RawMyBoards;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.model.db.Person;
import com.takefive.ledger.presenter.MainPresenter;
import com.takefive.ledger.view.IMainView;

import java.util.List;

/**
 * TODO: complete this JUNIT test case
 * Created by zyu on 3/19/16.
 */
public class TestMainPresenter {
    class FakeView implements IMainView {
        @Override
        public void showBillsList(List<RawBill> bills) {

        }

        @Override
        public void showMyBoards(RawMyBoards boards) {

        }

        @Override
        public void showMyUserInfo(RawPerson me) {

        }

        @Override
        public void showInfo(String str) {

        }

        @Override
        public void showInfo(int strId) {

        }
    }

    FakeView view = new FakeView();
    MainPresenter presenter = new MainPresenter();
}
