package com.takefive.ledger;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.takefive.ledger.database.UserStore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.profile_name_text)
    TextView mUserName;
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.chosen_account_view)
    FrameLayout mSideImgLayout;
    @Bind(R.id.chosen_account_content_view)
    RelativeLayout mSideImgContent;

    @Inject
    UserStore userStore;

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).inject(this);

        setSupportActionBar(mToolbar);

        mTitle = getTitle();
        mUserName.setText(getIntent().getStringExtra("username"));

        SimpleDateFormat dater = new SimpleDateFormat("dd/MM/yy HH:mm");

        try {
            mList.setAdapter(new SimpleAdapter(this, Arrays.asList(
                    new Data("zak", 12.22f, "Cravings", "Collected $24", dater.parse("02/12/15 12:50")),
                    new Data(null, 16.12f, "Circle K", "Collected $123", new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(3))),
                    new Data("mary", 56.22f, "Amazon", "Collected $12.22", new Date(new Date().getTime() - TimeUnit.HOURS.toMillis(1)))
            )));
        } catch (ParseException err) {
            err.printStackTrace();
        }


        // Fix drawer location after enabling status bar transparency.
        // Lollipop <=> v21, corresponding with values-v21
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.LayoutParams p = mSideImgLayout.getLayoutParams();
            p.height += getStatusBarHeight();
            mSideImgLayout.setLayoutParams(p);

            Helpers.setMargins(mSideImgContent, getStatusBarHeight(), null, null, null);
        }
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