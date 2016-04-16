package com.takefive.ledger.view;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.R;
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
    @Bind(R.id.newBillMembersListView)
    ListView mListView;
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
        mListView.setAdapter(adapter);

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
        private ViewGroup mListView;
        private HashMap<String, MoneyEdit> idToItemView = new HashMap<>();

        public void updatePersonAmountFromPresenter(String byId, Money amount) {
            notifyDataSetChanged();
        }

        public MembersSelectionAdapter(Context context, List<RawPerson> mInfoList) {
            super(context, R.layout.item_person_selection, mInfoList);
            this.mInfoList = mInfoList;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            mListView = parent;

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

                // Force using state of Presenter
                NewBillAmountPresenter.Info info = presenter.getPersonInfo(personId);
                if(info == null) {
                    mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                    mAmount.setAmount(zero);
                    mAmount.setEnabled(true);
                } else {
                    mAmount.setAmount(info.amount);
                    if (info.isAutoSplit) {
                        mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                        mAmount.setEnabled(false);
                    } else {
                        mAutoSplit.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                        mAmount.setEnabled(true);
                    }
                }

                mAmount.setOnEditorActionListener((v2, actionId, event) -> {
                    switch (actionId) {
                        case EditorInfo.IME_ACTION_DONE:
                        case EditorInfo.IME_ACTION_GO:
                        case EditorInfo.IME_ACTION_NEXT:
                        case EditorInfo.IME_ACTION_NONE:
                        case EditorInfo.IME_ACTION_PREVIOUS:
                        case EditorInfo.IME_ACTION_SEARCH:
                        case EditorInfo.IME_ACTION_SEND:
                        case EditorInfo.IME_ACTION_UNSPECIFIED:
                            presenter.inputAmountForPerson(person._id, mAmount.getAmount());
                            if(getActivity().getCurrentFocus() != null) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                            }
                            return true;
                        default:
                            return false;
                    }
                });
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
                            presenter.enableAutomaticAmountFor(person._id);
                        } else {
                            // the order of function calls are important
                            presenter.disableAutomaticAmountFor(person._id, true);
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
    }
}