package com.takefive.ledger.view;

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
import android.view.View;

import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.model.RawBill;
import com.takefive.ledger.model.RawBoard;
import com.takefive.ledger.model.RawMyBoards;
import com.takefive.ledger.presenter.MainPresenter;
import com.takefive.ledger.view.utils.NamedFragment;
import com.takefive.ledger.model.db.Person;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import zyu19.libs.action.chain.ActionChainFactory;

public class MainActivity extends AppCompatActivity implements IMainView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Inject
    MainBillFrag billFrag;

    MainBalanceFrag balanceFrag = new MainBalanceFrag();

    @Bind(R.id.navFrag)
    MainNavFrag navFrag;

    @Inject
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).inject(this);
        presenter.attachView(this);

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
        presenter.detachView();
        super.onDestroy();
    }

    void setupTabs() {
        StaticPagerAdapter adapter = new StaticPagerAdapter(this,
                billFrag.setTitle(getString(R.string.title_main_bill)),
                balanceFrag.setTitle(getString(R.string.title_main_balance)));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void showBillsList(List<RawBill> bills) {
        billFrag.showBillsList(bills);
    }

    @Override
    public void showMyBoards(RawMyBoards boards) {
        navFrag.showMyBoards(boards);
    }

    @Override
    public void showMyUserInfo(Person me) {
        navFrag.showMyUserInfo(me);
    }

    @Override
    public void showInfo(String str) {

    }

    @Override
    public void showInfo(int strId) {

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
