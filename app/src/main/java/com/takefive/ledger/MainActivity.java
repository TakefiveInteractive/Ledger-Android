package com.takefive.ledger;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;
import com.beardedhen.androidbootstrap.font.FontAwesome;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

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

        setSupportActionBar(mToolbar);

        mTitle = getTitle();
        mUserName.setText(getIntent().getStringExtra("username"));

        mList.setAdapter(new SimpleAdapter(this, Arrays.asList(
                new Data("zak", 12.22f, "Cravings", "Collected $24"),
                new Data(null, 16.12f, "Circle K", "Collected $123"),
                new Data("mary", 56.22f, "Amazon", "Collected $12.22")
        )));


        // Fix drawer location after enabling status bar transparency.
        ViewGroup.LayoutParams p = mSideImgLayout.getLayoutParams();
        p.height+=getStatusBarHeight();
        mSideImgLayout.setLayoutParams(p);

        Helpers.setMargins(mSideImgContent, getStatusBarHeight(), null, null, null);
    }

}

class Data {
    public Data(String a, float b, String c, String d) {
        whoPaid = a;
        paidAmount = b;
        desc1 = c;
        desc2 = d;
    }
    String whoPaid;
    float paidAmount;
    String desc1, desc2;
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

        whoPaid.setText(data.whoPaid == null ? "You paid:" : data.whoPaid + " paid:");
        paidAmount.setText("$" + data.paidAmount);
        desc1.setText(data.desc1);

        desc2.setBootstrapText(new BootstrapText.Builder(getContext())
                .addFontAwesomeIcon(FontAwesome.FA_CREDIT_CARD)
                .addText(data.desc2).build());

        return convertView;
    }
}