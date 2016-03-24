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
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.R;
import com.takefive.ledger.model.RawBill;
import com.takefive.ledger.view.utils.NamedFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBillFrag extends NamedFragment {

    @Bind(R.id.billList)
    ListView mList;

    BillDetailFragment mBillDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_main_bill, container, false);
        ButterKnife.bind(this, root);

        mBillDetail = new BillDetailFragment();

        // init list
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            mBillDetail.setContent((RawBill) mList.getItemAtPosition(position));
            mBillDetail.show(getFragmentManager(), "fragment_bill_detail");
        });

        return root;
    }

    public void showBillsList(List<RawBill> bills) {
        MainBillAdapter adapter = new MainBillAdapter(getContext(), bills);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

            TextView whoPaid = ButterKnife.findById(convertView, R.id.payer);
            TextView paidAmount = ButterKnife.findById(convertView, R.id.paidAmount);
            TextView desc1 = ButterKnife.findById(convertView, R.id.desc1);
            TextView desc2 = ButterKnife.findById(convertView, R.id.desc2);
            AwesomeTextView desc2icon = ButterKnife.findById(convertView, R.id.desc2icon);
            TextView time = ButterKnife.findById(convertView, R.id.time);

            ((MainActivity) getActivity()).presenter.loadUserInfo(data.recipient, info -> {
                boolean isMyself = data.recipient.equals(getActivity().getIntent().getStringExtra("username"));
                whoPaid.setText(isMyself ? "You paid:" : info.name + " paid:");

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
            });

            return convertView;
        }
    }
}
