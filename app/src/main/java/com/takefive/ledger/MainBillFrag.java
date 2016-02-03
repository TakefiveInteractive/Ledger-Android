package com.takefive.ledger;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.takefive.ledger.ui.NamedFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBillFrag extends NamedFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // We must attach to parent, otherwise material elevation won't work
        View root =  inflater.inflate(R.layout.frag_main_bill, container, true);
        ListView list = ButterKnife.findById(root, R.id.billList);

        SimpleDateFormat dater = new SimpleDateFormat("dd/MM/yy HH:mm");

        try {
            list.setAdapter(new SimpleAdapter(getContext(), Arrays.asList(
                    new Data("zak", 12.22f, "Cravings", "Collected $24", dater.parse("02/12/15 12:50")),
                    new Data(null, 16.12f, "Circle K", "Collected $123", new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(3))),
                    new Data("mary", 56.22f, "Amazon", "Collected $12.22", new Date(new Date().getTime() - TimeUnit.HOURS.toMillis(1)))
            )));
        } catch (ParseException err) {
            err.printStackTrace();
        }


        return root;
    }
}

class Data {
    public Data(String a, float b, String c, String d, Date e) {
        whoPaid = a;
        paidAmount = b;
        desc1 = c;
        desc2 = d;
        time = e;
    }
    String whoPaid;
    float paidAmount;
    String desc1, desc2;
    Date time;
}

class SimpleAdapter extends ArrayAdapter<Data> {
    public SimpleAdapter(Context context) {
        super(context, R.layout.item_main);
    }

    public SimpleAdapter(Context context, List<Data> objects) {
        super(context, R.layout.item_main, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_main, parent, false);

        Data data = getItem(position);

        TextView whoPaid = ButterKnife.findById(convertView, R.id.payer);
        TextView paidAmount = ButterKnife.findById(convertView, R.id.paidAmount);
        TextView desc1 = ButterKnife.findById(convertView, R.id.desc1);
        AwesomeTextView desc2 = ButterKnife.findById(convertView, R.id.desc2);
        TextView time = ButterKnife.findById(convertView, R.id.time);

        whoPaid.setText(data.whoPaid == null ? "You paid:" : data.whoPaid + " paid:");
        paidAmount.setText("$" + data.paidAmount);
        desc1.setText(data.desc1);

        desc2.setBootstrapText(new BootstrapText.Builder(getContext())
                .addFontAwesomeIcon(FontAwesome.FA_CREDIT_CARD)
                .addText(" " + data.desc2).build());

        time.setText(Helpers.shortDate(DateFormat.SHORT, data.time));

        return convertView;
    }
}
