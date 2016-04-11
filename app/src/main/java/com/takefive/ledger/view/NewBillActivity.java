package com.takefive.ledger.view;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.LinearLayout;

import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.midData.ledger.NewBillRequest;
import com.takefive.ledger.midData.ledger.PersonAmountPair;
import com.takefive.ledger.presenter.NewBillPresenter;
import com.takefive.ledger.view.database.SessionStore;
import com.takefive.ledger.view.utils.ConfirmableFragment;
import com.takefive.ledger.view.utils.ControllableViewPager;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewBillActivity extends AppCompatActivity implements INewBill,
        NewBillTitleFragment.OnConfirmListener,
        NewBillAmountFragment.OnConfirmListener {

    @Bind(R.id.newBillContent)
    LinearLayout mContent;
    @Bind(R.id.newBillToolbar)
    Toolbar mToolbar;
    @Bind(R.id.newBillPager)
    ControllableViewPager mPager;

    MenuItem mNext;
    MenuItem mCancel;

    @Inject
    NewBillPresenter presenter;

    boolean isAnimating;

    NewBillRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApplication) getApplication()).inject(this);
        presenter.attachView(this);

        setContentView(R.layout.activity_new_bill);
        ButterKnife.bind(this);

        overridePendingTransition(0, 0);

        isAnimating = false;

        request = new NewBillRequest();
        request.boardId = SessionStore.getDefault().activeBoardId;

        setSupportActionBar(mToolbar);
        mToolbar.setTitle("New Bill");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> close(false));

        mPager.setSwipeEnabled(false);
        mPager.setAdapter(new NewBillPageAdapter(getSupportFragmentManager(),
                new NewBillAmountFragment(),
                new NewBillTitleFragment(),
                new NewBillSubmitFragment()));
        // Change action bar buttons when appropriate
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setupButtons(position);
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

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    private void setupButtons(int position) {
        if (position == mPager.getAdapter().getCount() - 1) {
            mCancel.setVisible(false);
            mNext.setVisible(false);
            return;
        }
        String nextText = position == mPager.getAdapter().getCount() - 2 ? "Submit" : "Next";
        String prevText = position == 0 ? "Cancel" : "Back";
        mNext.setTitle(nextText);
        mCancel.setTitle(prevText);
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

    @Override
    public void doneAndClose() {
        nextSlide();
    }

    private void close(boolean successful) {
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
        Intent intent = getIntent();
        int resultCode = successful ? RESULT_OK : RESULT_CANCELED;
        setResult(resultCode, intent);
        anim.start();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_bill, menu);
        mCancel = menu.findItem(R.id.action_prev_slide);
        mNext = menu.findItem(R.id.action_next_slide);

        setupButtons(0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_next_slide:
                nextSlide();
                return true;
            case R.id.action_prev_slide:
                prevSlide();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        prevSlide();
    }

    private void submit() {
        presenter.createBill(request);
    }

    public void nextSlide() {
        int currentItem = mPager.getCurrentItem();
        if (!((NewBillPageAdapter) mPager.getAdapter()).getItem(currentItem).confirm())
            return;
        if (currentItem < mPager.getAdapter().getCount() - 2) {
            mPager.setCurrentItem(currentItem + 1);
        }
        else if (currentItem == mPager.getAdapter().getCount() - 2) {
            mPager.setCurrentItem(currentItem + 1);
            submit();
        }
        else {
            new Handler().postDelayed(() -> close(true), 1500);
        }
    }

    public void prevSlide() {
        int currentItem = mPager.getCurrentItem();
        if (currentItem != 0) {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
        else {
            close(false);
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

    @Override
    public void onConfirmTitle(String title, String description) {
        Log.d("NewBillActivity", "Title fragment confirmed with title " + title + " and description " + description);
        request.title = title;
        request.description = description;
    }

    @Override
    public void onConfirmAmount(double total, Map<String, Double> amounts) {
        Log.d("NewBillActivity", "Amount fragment confirmed");
        request.amounts = new ArrayList<>();
        for (Map.Entry<String, Double> entry : amounts.entrySet()) {
            request.amounts.add(new PersonAmountPair(entry.getKey(), entry.getValue()));
        }
    }

    private class NewBillPageAdapter extends FragmentPagerAdapter {

        ConfirmableFragment[] mFragments;

        public NewBillPageAdapter(FragmentManager fm, ConfirmableFragment... fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public ConfirmableFragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }

}
