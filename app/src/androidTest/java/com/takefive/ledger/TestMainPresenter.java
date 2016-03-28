package com.takefive.ledger;

import com.takefive.ledger.mid_data.ledger.RawBill;
import com.takefive.ledger.mid_data.ledger.RawMyBoards;
import com.takefive.ledger.mid_data.ledger.RawPerson;
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
        public void showAlert(String str) {

        }

        @Override
        public void showAlert(int strId) {

        }

        @Override
        public void setBoardTitle(String boardName) {

        }

        @Override
        public void stopRefreshing() {

        }

        @Override
        public void setCurrentBoardId(String id) {

        }
    }

    FakeView view = new FakeView();
    MainPresenter presenter = new MainPresenter();
}
