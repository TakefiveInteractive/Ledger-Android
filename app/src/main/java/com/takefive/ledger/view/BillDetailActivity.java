package com.takefive.ledger.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.takefive.ledger.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BillDetailActivity extends AppCompatActivity {

    private static final int ALPHA_ANIMATION_DURATION = 200;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.billDetailCollapsingLayout)
    CollapsingToolbarLayout mCollapsingLayout;
    @Bind(R.id.billDetailAppbarLayout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.billDetailToolbarTitle)
    TextView mTitle;
    @Bind(R.id.billDetailSummaryLayout)
    RelativeLayout mSummaryBar;
    @Bind(R.id.billDetailImage)
    ImageView mImage;
    @Bind(R.id.billDetailSummaryContainerLayout)
    LinearLayout mSummaryContainer;

    private boolean isTitleShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        ButterKnife.bind(this);

        Picasso.with(this).load(R.drawable.gradient_background).fit().into(mImage);

        AlphaAnimation titleAnimation = new AlphaAnimation(1f, 0f);
        titleAnimation.setDuration(0);
        titleAnimation.setFillAfter(true);
        mTitle.startAnimation(titleAnimation);

        // Begin summary animation
        int fromColor = ContextCompat.getColor(this, android.R.color.transparent);
        int toColor = ContextCompat.getColor(this, R.color.colorPrimary);
        ValueAnimator summaryAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        summaryAnimation.setDuration(250);
        summaryAnimation.addUpdateListener(animator -> mSummaryContainer.setBackgroundColor((int) animator.getAnimatedValue()));
        summaryAnimation.start();

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> supportFinishAfterTransition());
        mTitle.setText("Something really long");

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> changeToolbar(verticalOffset));

    }

    private void changeToolbar(int offset) {
        // Offset is negative
        // Log.d("BillDetail", "Offset: " + offset + ", toolbar height: " + mToolbar.getHeight() + ", appbar layout: " + mAppBarLayout.getHeight());
        AlphaAnimation titleAnimation, summaryAnimation;
        int barTop = mAppBarLayout.getHeight() + offset - mSummaryBar.getHeight();
        if (barTop <= mToolbar.getHeight() && !isTitleShown) {
            // Log.d("Title", "Shown");
            isTitleShown = true;
            titleAnimation = new AlphaAnimation(0f, 1f);
            summaryAnimation = new AlphaAnimation(1f, 0f);
        }
        else if (barTop >= mToolbar.getHeight() && isTitleShown) {
            // Log.d("Title", "Gone");
            isTitleShown = false;
            titleAnimation = new AlphaAnimation(1f, 0f);
            summaryAnimation = new AlphaAnimation(0f, 1f);
        }
        else {
            return;
        }

        titleAnimation.setDuration(ALPHA_ANIMATION_DURATION);
        summaryAnimation.setDuration(ALPHA_ANIMATION_DURATION);
        titleAnimation.setFillAfter(true);
        summaryAnimation.setFillAfter(true);
        mTitle.startAnimation(titleAnimation);
        mSummaryBar.startAnimation(summaryAnimation);
    }

}
