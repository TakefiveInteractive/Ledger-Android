package com.takefive.ledger.view;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.model.RawBill;
import com.takefive.ledger.model.RawMyBoards;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.presenter.FbUserInfo;
import com.takefive.ledger.presenter.MainPresenter;
import com.takefive.ledger.view.utils.NamedFragment;
import com.takefive.ledger.view.utils.PopupCardView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IMainView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    MainBillFrag billFrag = new MainBillFrag();

    MainBalanceFrag balanceFrag = new MainBalanceFrag();

    MainNavFrag navFrag = null;

    @Inject
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApplication) getApplication()).inject(this);
        presenter.attachView(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navFrag = (MainNavFrag) getSupportFragmentManager().findFragmentById(R.id.navFrag);
        ButterKnife.findById(navFrag.getView(), R.id.newBoard).setOnClickListener(v ->
                new NewBoardFragment().show(getSupportFragmentManager(), "fragment_new_board"));

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_drawer);
        mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));

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
    public void showMyUserInfo(RawPerson me) {
        navFrag.showMyUserInfo(me);
    }

    public void showAlert(String info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    public void showAlert(int info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
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
