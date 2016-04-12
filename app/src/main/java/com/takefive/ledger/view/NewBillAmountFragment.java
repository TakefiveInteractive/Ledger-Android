package com.takefive.ledger.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.R;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.view.utils.ConfirmableFragment;
import com.takefive.ledger.view.utils.MoneyEdit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBillAmountFragment extends ConfirmableFragment {

    @Bind(R.id.newBillAmount)
    MoneyEdit mAmount;
    @Bind(R.id.newBillMembersRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.newBillAmountLeft)
    TextView mAmountLeft;

    private MembersSelectionAdapter adapter;

    private Map<String, Double> amounts;

    private double total, collected;

    OnConfirmListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_bill_amount, container, false);
        ButterKnife.bind(this, root);

        amounts = new HashMap<>();
        total = 0;
        collected = 0;
        mAmount.setOnAmountChangeListener(amount -> {
            total = amount;
            calculateAndSetAmountLeft();
        });
        adapter = new MembersSelectionAdapter(getContext(), new ArrayList<>());
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        calculateAndSetAmountLeft();

        return root;
    }

    private void calculateAndSetAmountLeft() {
        mAmountLeft.setText(Helpers.currencyText(total - collected, getResources().getConfiguration().locale));
    }

    public List<RawPerson> getSelection() {
        return adapter.getSelection();
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
        if (total - collected != 0)
            return false;
        if (mAmount.getText().length() == 0 || adapter.getSelection().size() == 0 || amounts.size() == 0)
            return false;
        listener.onConfirmAmount(mAmount.getAmount(), amounts);
        return true;
    }

    public interface OnConfirmListener {
        void onConfirmAmount(double total, Map<String, Double> amounts);
    }

    private class MembersSelectionAdapter extends RecyclerView.Adapter<MembersSelectionAdapter.ViewHolder> {

        private Context mContext;
        private List<RawPerson> mInfoList;

        public MembersSelectionAdapter(Context context, List<RawPerson> infoList) {
            mContext = context;
            mInfoList = infoList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_person_selection, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 0) {
                holder.mNewPerson.setOnClickListener(v -> {
                    NewBillMembersFragment fragment = new NewBillMembersFragment();
                    fragment.setSelection(new ArrayList<>(amounts.keySet()));
                    fragment.setOnSelectionCompleteListener(selection -> {
                        Map<String, Double> newAmounts = new HashMap<>();
                        collected = 0;
                        for (RawPerson person : selection) {
                            newAmounts.put(person._id, 0.0);
                            if (amounts.containsKey(person._id)) {
                                double value = amounts.get(person._id);
                                newAmounts.put(person._id, value);
                                collected += value;
                            }
                        }
                        amounts = newAmounts;
                        mInfoList = selection;
                        notifyDataSetChanged();
                        calculateAndSetAmountLeft();
                    });
                    fragment.show(getFragmentManager(), "fragment_new_bill_members");
                });
                holder.emptyLayout.setVisibility(View.VISIBLE);
                holder.contentLayout.setVisibility(View.GONE);
            } else {
                --position;
                RawPerson person = mInfoList.get(position);
                holder.mAmount.setOnAmountChangeListener(amount -> {
                    if (amounts.containsKey(person._id))
                        collected += amount - amounts.get(person._id);
                    else
                        collected += amount;
                    amounts.put(person._id, amount);
                    calculateAndSetAmountLeft();
                });
                holder.mName.setText(person.name);
                Picasso.with(mContext).load(person.avatarUrl).into(holder.mAvatar);
                holder.emptyLayout.setVisibility(View.GONE);
                holder.contentLayout.setVisibility(View.VISIBLE);
            }
        }

        public List<RawPerson> getSelection() {
            return mInfoList;
        }

        @Override
        public int getItemCount() {
            return mInfoList.size() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            View emptyLayout;
            View contentLayout;
            Button mNewPerson;
            Button mReset;
            BootstrapCircleThumbnail mAvatar;
            TextView mName;
            MoneyEdit mAmount;

            public ViewHolder(View itemView) {
                super(itemView);
                emptyLayout = itemView.findViewById(R.id.personEmptyLayout);
                contentLayout = itemView.findViewById(R.id.personContentLayout);
                mNewPerson = (Button) itemView.findViewById(R.id.personAdd);
                mReset = (Button) itemView.findViewById(R.id.personReset);
                mAvatar = (BootstrapCircleThumbnail) itemView.findViewById(R.id.personAvatar);
                mName = (TextView) itemView.findViewById(R.id.personName);
                mAmount = (MoneyEdit) itemView.findViewById(R.id.personAmount);
            }

        }

    }
}