package com.takefive.ledger.view;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.takefive.ledger.Helpers;
import com.takefive.ledger.R;
import com.takefive.ledger.model.RawBill;

import java.text.DateFormat;
import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class BillDetailFragment extends DialogFragment {

    @Bind(R.id.billDescription)
    TextView mBillDesc;
    @Bind(R.id.billAmount)
    TextView mBillAmount;
    @Bind(R.id.billTime)
    TextView mBillTime;

    String description;
    String amount;
    String time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bill_detail, container, false);
        ButterKnife.bind(this, root);
        getDialog().setCanceledOnTouchOutside(true);

        mBillDesc.setText(description);
        mBillAmount.setText(amount);
        mBillTime.setText(time);

        return root;
    }

    public void setContent(RawBill bill) {
        description = bill.description;
        amount = "$" + bill.getTotalAmount();
        try {
            time = Helpers.longDate(DateFormat.MEDIUM, bill.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.closePopup)
    public void onCloseButtonClick() {
        this.dismiss();
    }

}
