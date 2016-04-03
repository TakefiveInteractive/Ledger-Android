package com.takefive.ledger.view;

import android.animation.Animator;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewBillActivity extends AppCompatActivity implements INewBill {

    @Bind(R.id.newBillContent)
    LinearLayout mContent;
    @Bind(R.id.newBillToolbar)
    Toolbar mToolbar;
    @Bind(R.id.newBillPager)
    ViewPager mPager;
    @Bind(R.id.newBillSubmit)
    Button mNext;
    @Bind(R.id.newBillCancel)
    Button mCancel;

    boolean isAnimating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);
        ButterKnife.bind(this);
        // ((MyApplication) getApplication()).inject(this);

        overridePendingTransition(0, 0);

        isAnimating = false;

        setSupportActionBar(mToolbar);
        mToolbar.setTitle("New Bill");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> close());

        mPager.setAdapter(new NewBillPageAdapter(getSupportFragmentManager(),
                new NewBillTitleFragment()));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String nextText = position == mPager.getAdapter().getCount() - 1 ? "Submit" : "Next";
                String prevText = position == 0 ? "Cancel" : "Back";
                mNext.setText(nextText);
                mCancel.setText(prevText);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (savedInstanceState == null) {

            mContent.setVisibility(View.INVISIBLE);
            ViewTreeObserver vto = mContent.getViewTreeObserver();
            if (vto.isAlive()) {
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        expandCircle();
                        mContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    private void expandCircle() {
        Intent intent = getIntent();
        int[] location;
        if (intent.hasExtra("revealLocation"))
            location = intent.getIntArrayExtra("revealLocation");
        else {
            location = new int[2];
            location[0] = mContent.getWidth() / 2;
            location[1] = mContent.getHeight() / 2;
        }
        double startingRadius = intent.getDoubleExtra("revealStartingRadius", 0);
        double endingRadius = Math.hypot(location[0], location[1]);
        Log.d("NewBillActivity open", "Animation with location x: " + location[0]
                + " y: " + location[1] + " ending: " + endingRadius);

        Animator anim = ViewAnimationUtils.createCircularReveal(
                mContent, location[0], location[1], 0, (float) endingRadius);
        anim.setDuration(600);
        mContent.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void close() {
        if (isAnimating)
            return;

        int[] location = new int[2];
        location[0] = mContent.getWidth() / 2;
        location[1] = mContent.getHeight() / 2;
        double startingRadius = Math.hypot(location[0], location[1]);
        Log.d("NewBillActivity close", "Animation with location x: " + location[0]
                + " y: " + location[1] + " starting: " + startingRadius);
        Animator anim = ViewAnimationUtils.createCircularReveal(
                mContent, location[0], location[1], (float) startingRadius, 0);
        anim.setDuration(550);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mContent.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAnimating = false;
            }
        });
        anim.start();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        prevSlide();
    }

    private void submit() {
        // TODO: Connect to server
    }

    @Override
    @OnClick(R.id.newBillSubmit)
    public void nextSlide() {
        int currentItem = mPager.getCurrentItem();
        if (currentItem != mPager.getAdapter().getCount() - 1) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
        else {
            submit();
            close();
        }
    }

    @Override
    @OnClick(R.id.newBillCancel)
    public void prevSlide() {
        int currentItem = mPager.getCurrentItem();
        if (currentItem != 0) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
        else {
            close();
        }
    }

    @Override
    public void showAlert(String info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAlert(int info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    private class NewBillPageAdapter extends FragmentPagerAdapter {

        Fragment[] mFragments;

        public NewBillPageAdapter(FragmentManager fm, Fragment... fragments) {
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
    }
}
