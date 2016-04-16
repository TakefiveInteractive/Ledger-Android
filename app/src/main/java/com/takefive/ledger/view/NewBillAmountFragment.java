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
import android.widget.Button;
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
    @Bind(R.id.newBillMembersRecyclerView)
    RecyclerView mRecyclerView;
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
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    public List<RawPerson> getSelection() {
        return adapter.getSelection();
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
        adapter.getPersonAmountEdit(id).setText(amount.toString());
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

    private class MembersSelectionAdapter extends RecyclerView.Adapter<MembersSelectionAdapter.ViewHolder> {

        private Context mContext;
        private List<RawPerson> mInfoList;
        private HashMap<String, MoneyEdit> idToItemView = new HashMap<>();


        // WRITE these ONLY when the view is detached
        // READ/DELETE these ONLY when the view is reattached.
        private HashMap<String, Boolean> cacheIsAutoSplit = new HashMap<>();
        private HashMap<String, Money> cacheHandInput = new HashMap<>();

        public MoneyEdit getPersonAmountEdit(String byId) {
            if (!idToItemView.containsKey(byId))
                return null;
            return idToItemView.get(byId);
        }


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
                    fragment.setSelection(StreamSupport.stream(mInfoList
                    ).map(person -> person._id).collect(Collectors.toList()));
                    fragment.setOnSelectionCompleteListener(selection -> {
                        mInfoList = selection;
                        notifyDataSetChanged();
                    });
                    fragment.show(getFragmentManager(), "fragment_new_bill_members");
                });
                holder.emptyLayout.setVisibility(View.VISIBLE);
                holder.contentLayout.setVisibility(View.GONE);
            } else {
                --position;
                RawPerson person = mInfoList.get(position);
                final MoneyEdit.OnAmountChangeListener amountChangeListener = amount -> {
                    presenter.inputAmountForPerson(person._id, amount);
                };
                holder.mPersonId = person._id;
                holder.mAmount.setOnAmountChangeListener(amountChangeListener);
                holder.mName.setText(person.name);
                Picasso.with(mContext).load(person.avatarUrl).into(holder.mAvatar);
                holder.emptyLayout.setVisibility(View.GONE);
                holder.contentLayout.setVisibility(View.VISIBLE);
                holder.mAutoSplit.setOnClickListener(v -> {
                    synchronized (adapter) {
                        if (holder.mAmount.isEnabled()) {
                            // the order of function calls are important
                            holder.mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                            holder.mAmount.setEnabled(false);
                            holder.mAmount.setOnAmountChangeListener(null);
                            presenter.enableAutomaticAmountFor(person._id);
                        } else {
                            // the order of function calls are important
                            presenter.disableAutomaticAmountFor(person._id, true);
                            holder.mAmount.setOnAmountChangeListener(amountChangeListener);
                            holder.mAmount.setEnabled(true);
                            holder.mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                        }
                    }
                });

                idToItemView.put(person._id, holder.mAmount);
            }
        }

        public List<RawPerson> getSelection() {
            return mInfoList;
        }

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
                holder.mAmount.setText(cacheHandInput.get(personId).toString());
                presenter.inputAmountForPerson(personId, cacheHandInput.get(personId));
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

    }
}