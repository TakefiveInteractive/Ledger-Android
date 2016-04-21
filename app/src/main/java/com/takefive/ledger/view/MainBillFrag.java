package com.takefive.ledger.view;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.takefive.ledger.midData.ledger.RawBill;
import com.takefive.ledger.midData.view.ShownBill;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.view.database.SessionStore;
import com.takefive.ledger.view.utils.ExtendedSwipeRefreshLayout;
import com.takefive.ledger.view.utils.NamedFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zyu19.libs.action.chain.ActionChainFactory;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBillFrag extends NamedFragment {

    public static final int NEW_BILL_REQUEST = 0;

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

    List<ShownBill> mBills;

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
            /*
            // TODO: convert DetailFragment to use ShownBill
            mBillDetail.setContent(((ShownBill)mList.getItemAtPosition(position)).rawBill);
            mBillDetail.show(getFragmentManager(), "fragment_bill_detail");
            */

            Intent intent = new Intent(getActivity(), BillDetailActivity.class);
            intent.putExtra("billId", mBills.get(position).rawBill._id);
            // Shared element is not used.
            /*
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                String transitionName = getString(R.string.bill_to_detail);
                startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this.getActivity(), view, transitionName).toBundle());
            }
            else {
                startActivity(intent);
            }
            */
            startActivity(intent);
        });

        mNew.attachToListView(mList);

        mSwipeLayout.setListView(mList);
        mSwipeLayout.setOnRefreshListener(() -> ((MainActivity) getActivity()).presenter.loadBills(SessionStore.getDefault().activeBoardId));

        return root;
    }

    @OnClick(R.id.billNew)
    public void onNewBillButtonClicked() {
        double radius = ((double) mNew.getWidth()) / 2;
        int[] location = new int[2];
        mNew.getLocationOnScreen(location);

        Intent intent = new Intent(getActivity(), NewBillActivity.class);
        intent.putExtra("revealLocation", location);
        intent.putExtra("revealStartRadius", radius);

        startActivityForResult(intent, NEW_BILL_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_BILL_REQUEST:
                switch (resultCode) {
                    case RESULT_OK:
                        ((MainActivity) getActivity()).presenter.loadBills(SessionStore.getDefault().activeBoardId);
                        mSwipeLayout.setRefreshing(true);
                        break;
                    case RESULT_CANCELED:
                    default:
                        break;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void showBillsList(List<ShownBill> bills) {
        mBills = bills;
        MainBillAdapter adapter = new MainBillAdapter(getContext(), mBills);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void startRefreshing() {
        if (!mSwipeLayout.isRefreshing())
            mSwipeLayout.setRefreshing(true);
    }

    public void stopRefreshing() {
        if (mSwipeLayout.isRefreshing())
            mSwipeLayout.setRefreshing(false);
    }


    class MainBillAdapter extends ArrayAdapter<ShownBill> {

        public MainBillAdapter(Context context, List<ShownBill> objects) {
            super(context, R.layout.item_bill_list, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bill_list, parent, false);

            ShownBill data = getItem(position);
            RawBill rawBill = data.rawBill;

            BootstrapCircleThumbnail avatar = ButterKnife.findById(convertView, R.id.avatar);
            TextView title = ButterKnife.findById(convertView, R.id.title);
            TextView amount = ButterKnife.findById(convertView, R.id.amount);
            TextView time = ButterKnife.findById(convertView, R.id.time);
            TextView recipient = ButterKnife.findById(convertView, R.id.recipient);

            // Set title
            title.setText(rawBill.title);

            // Set recipient
            String recipientName =
                    rawBill.recipient.equals(userStore.getMostRecentUserId()) ? "you" : data.recipientName;
            recipient.setText("paid by " + recipientName);

            // Set avatar
            if (data.recipientAvatarUrl != null)
                Picasso.with(getContext()).load(data.recipientAvatarUrl).fit().into(avatar);

            // Set amount
            amount.setText("$" + rawBill.getTotalAmount());

            // Set time
            try {
                time.setText(Helpers.shortDate(DateFormat.SHORT, rawBill.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
