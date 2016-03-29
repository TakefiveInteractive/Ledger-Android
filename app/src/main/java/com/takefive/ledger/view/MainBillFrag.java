package com.takefive.ledger.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.mid_data.ledger.RawBill;
import com.takefive.ledger.mid_data.ledger.RawPerson;
import com.takefive.ledger.model.Person;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.view.utils.ExtendedSwipeRefreshLayout;
import com.takefive.ledger.view.utils.NamedFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBillFrag extends NamedFragment {

    @Bind(R.id.billList)
    ListView mList;
    @Bind(R.id.billSwipeRefreshLayout)
    ExtendedSwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.billNone)
    View mNone;

    @Bind(R.id.billNew)
    FloatingActionButton mNew;

    @Inject
    RealmAccess realmAccess;

    @Inject
    UserStore userStore;

    @Inject
    ActionChainFactory chainFactory;

    BillDetailFragment mBillDetail;

    String currentBoardId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_main_bill, container, false);
        ((MyApplication) getActivity().getApplication()).inject(this);
        ButterKnife.bind(this, root);

        mBillDetail = new BillDetailFragment();

        // init list
        mList.setEmptyView(mNone);
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            mBillDetail.setContent((RawBill) mList.getItemAtPosition(position));
            mBillDetail.show(getFragmentManager(), "fragment_bill_detail");
        });

        mNew.attachToListView(mList);

        mSwipeLayout.setListView(mList);
        mSwipeLayout.setOnRefreshListener(() -> ((MainActivity) getActivity()).presenter.loadBills(currentBoardId));

        return root;
    }

    public String getCurrentBoardId() {
        return currentBoardId;
    }

    public void setCurrentBoardId(String currentBoardId) {
        this.currentBoardId = currentBoardId;
    }

    public void showBillsList(List<RawBill> bills) {
        MainBillAdapter adapter = new MainBillAdapter(getContext(), bills);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void stopRefreshing() {
        if (mSwipeLayout.isRefreshing())
            mSwipeLayout.setRefreshing(false);
    }


    class MainBillAdapter extends ArrayAdapter<RawBill> {

        public MainBillAdapter(Context context, List<RawBill> objects) {
            super(context, R.layout.item_bill_list, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bill_list, parent, false);

            RawBill data = getItem(position);

            BootstrapCircleThumbnail avatar = ButterKnife.findById(convertView, R.id.avatar);
            TextView whoPaid = ButterKnife.findById(convertView, R.id.payer);
            TextView paidAmount = ButterKnife.findById(convertView, R.id.paidAmount);
            TextView desc1 = ButterKnife.findById(convertView, R.id.desc1);
            TextView desc2 = ButterKnife.findById(convertView, R.id.desc2);
            AwesomeTextView desc2icon = ButterKnife.findById(convertView, R.id.desc2icon);
            TextView time = ButterKnife.findById(convertView, R.id.time);

            if (data.recipient.equals(userStore.getMostRecentUserId())) {
                whoPaid.setText("You paid:");
                chainFactory.get(fail -> {
                    fail.getCause().printStackTrace();
                    ((MainActivity) getActivity()).showAlert(fail.getCause().getMessage());
                }).netThen(() -> realmAccess.process(realm -> {
                    Person me = realm.where(Person.class)
                            .equalTo("personId", userStore.getMostRecentUserId()).findFirst();
                    if (!me.load())
                        throw new Exception(getString(R.string.ex_cannot_load_realm));
                    return me.getAvatarUrl();
                })).uiConsume((String url) -> {
                    Picasso.with(getContext()).load(url).fit().into(avatar);
                }).start();
            } else {
                chainFactory.get(fail -> {
                    fail.getCause().printStackTrace();
                    ((MainActivity) getActivity()).showAlert(fail.getCause().getMessage());
                }).netThen(() -> realmAccess.process(realm -> {
                    Person recipient = realm.where(Person.class).equalTo("personId", data.recipient).findFirst();
                    if(!recipient.load())
                        throw new Exception(getString(R.string.ex_cannot_load_realm));

                    RawPerson detached = new RawPerson();
                    detached.name = recipient.getName();
                    detached.avatarUrl = recipient.getAvatarUrl();
                    return detached;
                })).uiConsume((RawPerson recipient) -> {
                    if (recipient != null && recipient.name != null) {
                        whoPaid.setText(recipient.name + "\npaid:");
                        if (recipient.avatarUrl != null)
                            Picasso.with(getContext()).load(recipient.avatarUrl).fit().into(avatar);
                    } else {
                        ((MainActivity) getActivity()).presenter.loadUserInfo(data.recipient, info -> {
                            whoPaid.setText(info.name + "\npaid:");
                            if (info.avatarUrl != null)
                                Picasso.with(getContext()).load(info.avatarUrl).fit().into(avatar);
                        });
                    }
                }).start();
            }

            paidAmount.setText("$" + data.getTotalAmount());
            desc1.setText(data.title);

            desc2icon.setBootstrapText(new BootstrapText.Builder(getContext())
                    .addFontAwesomeIcon(FontAwesome.FA_CREDIT_CARD).build());
            desc2.setText(" " + data.description);

            try {
                time.setText(Helpers.shortDate(DateFormat.SHORT, data.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
