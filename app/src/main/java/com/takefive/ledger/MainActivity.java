package com.takefive.ledger;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.takefive.ledger.ui.NamedFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.profile_name_text)
    TextView mUserName;
    @Bind(R.id.chosen_account_view)
    FrameLayout mSideImgLayout;
    @Bind(R.id.chosen_account_content_view)
    RelativeLayout mSideImgContent;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

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
        // ((MyApplication) getApplication()).inject(this);

        setSupportActionBar(mToolbar);

        // Fix drawer location after enabling status bar transparency.
        // Lollipop <=> v21, corresponding with values-v21
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.LayoutParams p = mSideImgLayout.getLayoutParams();
            p.height += getStatusBarHeight();
            mSideImgLayout.setLayoutParams(p);

            Helpers.setMargins(mSideImgContent, getStatusBarHeight(), null, null, null);
        }

        setupTabs();
        mUserName.setText(getIntent().getStringExtra("username"));
    }

    void setupTabs() {
        StaticPagerAdapter adapter = new StaticPagerAdapter(getSupportFragmentManager(),
                new MainBillFrag().setTitle(getString(R.string.title_main_bill)),
                new MainBalanceFrag().setTitle(getString(R.string.title_main_balance)));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}

class StaticPagerAdapter extends FragmentPagerAdapter {
    NamedFragment[] mFragments;

    public StaticPagerAdapter(FragmentManager fm, NamedFragment... fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments[position].getTitle();
    }
}
