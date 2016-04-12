package com.takefive.ledger.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.takefive.ledger.R;
import com.takefive.ledger.view.utils.ConfirmableFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBillTitleFragment extends ConfirmableFragment {

    @Bind(R.id.newBillTitle)
    TextView mTitle;
    @Bind(R.id.newBillDescription)
    TextView mDescription;

    OnConfirmListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_bill_title, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnConfirmListener) context;
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean confirm() {
        if (mTitle.getText().length() == 0)
            return false;
        String description = mDescription.length() == 0 ? "" : mDescription.getText().toString();
        listener.onConfirmTitle(mTitle.getText().toString(), description);
        return true;
    }

    public interface OnConfirmListener {
        void onConfirmTitle(String title, String description);
    }

}
