package com.takefive.ledger;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import com.takefive.ledger.database.RealmAccess;
import com.takefive.ledger.database.UserStore;
import com.takefive.ledger.model.Person;
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
import javax.inject.Provider;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import zyu19.libs.action.chain.ActionChainFactory;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Inject
    ActionChainFactory chainFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).inject(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_drawer);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        setupTabs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void setupTabs() {
        StaticPagerAdapter adapter = new StaticPagerAdapter(this,
                new MainBillFrag().setTitle(getString(R.string.title_main_bill)),
                new MainBalanceFrag().setTitle(getString(R.string.title_main_balance)));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}

class StaticPagerAdapter extends FragmentPagerAdapter {
    NamedFragment[] mFragments;

    public static String getBundleNameFor(NamedFragment fragment) {
        return getBundleNameFor(fragment.getTitle());
    }

    public static String getBundleNameFor(String fragmentTitle) {
        return "NamedFragment_" + fragmentTitle;
    }

    public StaticPagerAdapter(AppCompatActivity context, NamedFragment... fragments) {
        super(context.getSupportFragmentManager());
        FragmentManager fm = context.getSupportFragmentManager();
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
