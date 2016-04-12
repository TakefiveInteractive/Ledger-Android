package com.takefive.ledger.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takefive.ledger.R;
import com.takefive.ledger.view.utils.ConfirmableFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBillSubmitFragment extends ConfirmableFragment {

    @Bind(R.id.newBillSubmitProgressLayout)
    View mProgressLayout;

    @Bind(R.id.newBillSubmitDoneLayout)
    View mDoneLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_bill_submit, container, false);
        ButterKnife.bind(this, root);
        mProgressLayout.setVisibility(View.VISIBLE);
        mDoneLayout.setVisibility(View.GONE);
        return root;
    }

    @Override
    public boolean confirm() {
        mProgressLayout.setVisibility(View.GONE);
        mDoneLayout.setVisibility(View.VISIBLE);
        return true;
    }

}
