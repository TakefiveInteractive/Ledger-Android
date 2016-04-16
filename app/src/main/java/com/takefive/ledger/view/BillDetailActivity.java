package com.takefive.ledger.view;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    private boolean isTitleShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        ButterKnife.bind(this);

        AlphaAnimation initialAnimation = new AlphaAnimation(1f, 0f);
        initialAnimation.setDuration(0);
        initialAnimation.setFillAfter(true);
        mTitle.startAnimation(initialAnimation);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> supportFinishAfterTransition());
        mTitle.setText("Something really long");

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percentage = Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
            changeToolbar(verticalOffset);
        });

    }

    private void changeToolbar(int offset) {
        // Offset is negative
        // Log.d("BillDetail", "Offset: " + offset + ", toolbar height: " + mToolbar.getHeight() + ", appbar layout: " + mAppBarLayout.getHeight());
        AlphaAnimation animation;
        int barTop = mAppBarLayout.getHeight() + offset - mSummaryBar.getHeight();
        if (barTop <= 0 && !isTitleShown) {
            Log.d("Title", "Shown");
            isTitleShown = true;
            animation = new AlphaAnimation(0f, 1f);
        }
        else if (barTop >= 0 && isTitleShown) {
            Log.d("Title", "Gone");
            isTitleShown = false;
            animation = new AlphaAnimation(1f, 0f);
        }
        else {
            return;
        }

        animation.setDuration(ALPHA_ANIMATION_DURATION);
        animation.setFillAfter(true);
        mTitle.startAnimation(animation);
    }

}
