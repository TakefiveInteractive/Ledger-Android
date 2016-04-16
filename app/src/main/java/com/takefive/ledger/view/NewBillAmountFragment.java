package com.takefive.ledger.view;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.ledger.Helper;
import com.takefive.ledger.midData.Money;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.presenter.NewBillAmountPresenter;
import com.takefive.ledger.view.utils.ConfirmableFragment;
import com.takefive.ledger.view.utils.MoneyEdit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBillAmountFragment extends ConfirmableFragment implements INewBillAmountView {

    @Bind(R.id.newBillAmount)
    MoneyEdit mTotalAmount;
    @Bind(R.id.newBillMembersListView)
    ListView mRecyclerView;
    @Bind(R.id.newBillAmountLeft)
    TextView mAmountLeft;

    private MembersSelectionAdapter adapter;

    private Money zero;

    private Locale locale;

    OnConfirmListener listener;

    NewBillAmountPresenter presenter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_bill_amount, container, false);
        ButterKnife.bind(this, root);

        locale = getResources().getConfiguration().locale;
        zero = new Money(locale, 0);
        presenter = new NewBillAmountPresenter(locale);

        mTotalAmount.setOnAmountChangeListener(presenter::setTotalAmount);
        adapter = new MembersSelectionAdapter(getContext(), new ArrayList<>());
        /*
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        */
        mRecyclerView.setAdapter(adapter);

        // Initialize all texts
        presenter.attachView(this);
        presenter.setTotalAmount(zero);

        return root;
    }

    @Override
    public void onStart() {
        presenter.attachView(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        presenter.detachView();
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnConfirmListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean confirm() {
        if (!presenter.canConfirm())
            return false;
        Map<String, Money> amounts = presenter.getAssignments();
        if (mTotalAmount.getText().length() == 0 || adapter.getSelection().size() == 0 || amounts.size() == 0)
            return false;
        listener.onConfirmAmount(mTotalAmount.getAmount(), amounts);
        return true;
    }

    @Override
    public void updateAmountForPerson(String id, Money amount) {
        adapter.updatePersonAmountFromPresenter(id, amount);
    }

    @Override
    public void updateRemainingAmount(Money amount) {
        mAmountLeft.setText(amount.toString());
    }

    @Override
    public void showAlert(String str) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), str, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAlert(int strId) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), strId, Snackbar.LENGTH_SHORT).show();
    }

    public interface OnConfirmListener {
        void onConfirmAmount(Money total, Map<String, Money> amounts);
    }

    private class MembersSelectionAdapter extends ArrayAdapter<RawPerson> {

        private Context mContext;
        private List<RawPerson> mInfoList;
        private HashMap<String, MoneyEdit> idToItemView = new HashMap<>();
        private HashMap<String, Money> person2Amount = new HashMap<>();
        private HashMap<String, Boolean> isAutoSplit = new HashMap<>();

        public void updatePersonAmountFromPresenter(String byId, Money amount) {
            if (!idToItemView.containsKey(byId))
                return;
            idToItemView.get(byId).setAmount(amount);
        }

        public MembersSelectionAdapter(Context context, List<RawPerson> mInfoList) {
            super(context, R.layout.item_person_selection, mInfoList);
            this.mInfoList = mInfoList;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_person_selection, parent, false);

            View emptyLayout = ButterKnife.findById(convertView, R.id.personEmptyLayout);
            View contentLayout = ButterKnife.findById(convertView, R.id.personContentLayout);
            Button mNewPerson = ButterKnife.findById(convertView, R.id.personAdd);
            Button mReset = ButterKnife.findById(convertView, R.id.personReset);
            AwesomeTextView mAutoSplit = ButterKnife.findById(convertView, R.id.autoSplit);
            BootstrapCircleThumbnail mAvatar = ButterKnife.findById(convertView, R.id.personAvatar);
            TextView mName = ButterKnife.findById(convertView, R.id.personName);
            MoneyEdit mAmount = ButterKnife.findById(convertView, R.id.personAmount);

            if(position == 0) {
                mNewPerson.setOnClickListener(v -> {
                    NewBillMembersFragment fragment = new NewBillMembersFragment();
                    fragment.setSelection(StreamSupport.stream(mInfoList
                    ).map(person -> person._id).collect(Collectors.toList()));
                    fragment.setOnSelectionCompleteListener(selection -> {
                        presenter.clearExceptTotalAmount();
                        idToItemView.clear();
                        person2Amount.clear();
                        isAutoSplit.clear();
                        mInfoList = selection;
                        notifyDataSetChanged();
                    });
                    fragment.show(getFragmentManager(), "fragment_new_bill_members");
                });
                emptyLayout.setVisibility(View.VISIBLE);
                contentLayout.setVisibility(View.GONE);
            } else {
                --position;
                RawPerson person = mInfoList.get(position);
                String personId = person._id;

                // Force remove state of autoSplit and inputted value.
                // Could also use a cache, but because Android destroy views as soon as invisible, not very viable.
                if(isAutoSplit.containsKey(personId)) {
                    mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                    mAmount.setAmount(zero);
                    mAmount.setEnabled(true);
                }
                mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                mAmount.setAmount(zero);
                mAmount.setEnabled(true);

                final MoneyEdit.OnAmountChangeListener amountChangeListener = amount -> {
                    presenter.inputAmountForPerson(person._id, amount);
                };
                mAmount.setOnAmountChangeListener(amountChangeListener);
                mName.setText(person.name);
                Picasso.with(mContext).load(person.avatarUrl).into(mAvatar);
                emptyLayout.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                mAutoSplit.setOnClickListener(v -> {
                    synchronized (adapter) {
                        if (mAmount.isEnabled()) {
                            // the order of function calls are important
                            mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                            mAmount.setEnabled(false);
                            mAmount.setOnAmountChangeListener(null);
                            presenter.enableAutomaticAmountFor(person._id);
                        } else {
                            // the order of function calls are important
                            presenter.disableAutomaticAmountFor(person._id, true);
                            mAmount.setOnAmountChangeListener(amountChangeListener);
                            mAmount.setEnabled(true);
                            mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                        }
                    }
                });

                idToItemView.put(person._id, mAmount);
            }

            return convertView;
        }

        public List<RawPerson> getSelection() {
            return mInfoList;
        }

        @Override
        public int getCount() {
            return mInfoList.size() + 1;
        }

        /*
        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            Log.d("NewBillRecycle", "Attach " + holder.mName.getText() + " with id " + holder.mPersonId);

            // Force NOT recycling data
            String personId = holder.mPersonId;
            if (cacheIsAutoSplit.containsKey(personId) && cacheIsAutoSplit.get(personId)) {
                // force override view state.
                holder.mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                holder.mAmount.setEnabled(false);

                // This will help us override amount text
                presenter.enableAutomaticAmountFor(personId);
            } else if (cacheHandInput.containsKey(personId)) {
                presenter.disableAutomaticAmountFor(personId, false);

                // force override view state.
                holder.mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                holder.mAmount.setEnabled(true);
                holder.mAmount.setAmount(cacheHandInput.get(personId));
                presenter.inputAmountForPerson(personId, cacheHandInput.get(personId));
            } else {
                // This should be the case where the attached item is a totally new data.
                // We should clear all View states for it.
                holder.mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                holder.mAmount.setEnabled(true);
                holder.mAmount.setAmount(zero);
            }

            // Clear this person's cache because its mission has ended
            cacheHandInput.remove(personId);
            cacheIsAutoSplit.remove(personId);

            Log.d("NewBillRecycle", "Presenter = " + presenter.debugString());
            super.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            Log.d("NewBillRecycle", "Detach " + holder.mName.getText() + " with id " + holder.mPersonId);

            // Prepare for: Force NOT recycling data
            MoneyEdit edit = holder.mAmount;
            String personId = holder.mPersonId;
            if (edit.isEnabled()) {
                cacheHandInput.put(personId, edit.getAmount());
                cacheIsAutoSplit.put(personId, false);
                presenter.inputAmountForPerson(personId, zero);
            } else {
                cacheIsAutoSplit.put(personId, true);
                presenter.disableAutomaticAmountFor(personId, false);
            }
            Log.d("NewBillRecycle", "Presenter = " + presenter.debugString());
            super.onViewDetachedFromWindow(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return super.onFailedToRecycleView(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            super.onViewRecycled(holder);
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
            AwesomeTextView mAutoSplit;
            BootstrapCircleThumbnail mAvatar;
            TextView mName;
            MoneyEdit mAmount;
            String mPersonId;

            public ViewHolder(View itemView) {
                super(itemView);
                this.mPersonId = null;
                emptyLayout = itemView.findViewById(R.id.personEmptyLayout);
                contentLayout = itemView.findViewById(R.id.personContentLayout);
                mNewPerson = (Button) itemView.findViewById(R.id.personAdd);
                mReset = (Button) itemView.findViewById(R.id.personReset);
                mAvatar = (BootstrapCircleThumbnail) itemView.findViewById(R.id.personAvatar);
                mName = (TextView) itemView.findViewById(R.id.personName);
                mAmount = (MoneyEdit) itemView.findViewById(R.id.personAmount);
                mAutoSplit = ButterKnife.findById(itemView, R.id.autoSplit);
            }


        }
        */

    }
}