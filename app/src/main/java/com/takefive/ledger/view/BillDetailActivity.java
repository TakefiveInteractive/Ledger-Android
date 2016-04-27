package com.takefive.ledger.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.midData.ledger.RawAmount;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.midData.view.ShownAmount;
import com.takefive.ledger.midData.view.ShownBillInflated;
import com.takefive.ledger.model.Person;
import com.takefive.ledger.presenter.BillDetailPresenter;
import com.takefive.ledger.presenter.utils.DateTimeConverter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import java8.util.stream.StreamSupport;

public class BillDetailActivity extends AppCompatActivity implements IBillDetail {

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

    @Bind(R.id.billDetailAmounts)
    RecyclerView mRecycler;

    @Bind(R.id.billDetailTitle)
    TextView mBillTitle;

    @Bind(R.id.billDetailAmount)
    TextView mBillAmount;

    @Bind(R.id.billDetailTime)
    TextView mBillTime;

    @Bind(R.id.billDetailRecipient)
    TextView mBillRecipient;

    @Bind(R.id.billDetailDescription)
    TextView mBillDescription;

    @Inject
    BillDetailPresenter presenter;

    private boolean isTitleShown = false;

    private class BillDetailAmountsItemAdapter extends RecyclerView.Adapter<BillDetailAmountsItemAdapter.ViewHolder> {

        List<ShownAmount> amounts;

        class ViewHolder extends RecyclerView.ViewHolder {

            BootstrapCircleThumbnail mAvatar;
            TextView mName;
            TextView mAmount;

            public ViewHolder(View itemView) {
                super(itemView);
                mAvatar = (BootstrapCircleThumbnail) itemView.findViewById(R.id.billDetailAmountItemAvatar);
                mName = (TextView) itemView.findViewById(R.id.billDetailAmountItemName);
                mAmount = (TextView) itemView.findViewById(R.id.billDetailAmountItemAmount);
            }
        }

        public BillDetailAmountsItemAdapter(List<ShownAmount> amounts) {
            this.amounts = amounts;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bill_detail_amount, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ShownAmount shownAmount = amounts.get(position);
            holder.mAmount.setText("$" + shownAmount.amount);
            holder.mName.setText(shownAmount.personName);
            Picasso.with(BillDetailActivity.this)
                .load(shownAmount.personAvatarUrl)
                .placeholder(R.drawable.person_image_empty)
                .fit()
                .into(holder.mAvatar);
        }

        @Override
        public int getItemCount() {
            return amounts.size();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApplication) getApplication()).inject(this);
        presenter.attachView(this);

        setContentView(R.layout.activity_bill_detail);
        ButterKnife.bind(this);

        Picasso.with(this).load(R.drawable.gradient_background).fit().into(mImage);

        // Animate title to its initial state
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

        // Toolbar setup
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> supportFinishAfterTransition());

        // Setup collapse listener
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> changeToolbar(verticalOffset));

        // Setup RecyclerView
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecycler.setHasFixedSize(true);

        // Get bill ID from intent and inflate.
        Intent intent = getIntent();
        String billId = intent.getStringExtra("billId");
        presenter.inflateBillDetail(billId);

    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
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

    @Override
    public void showAmounts(ShownBillInflated bill) {
        mTitle.setText(bill.rawBill.title);
        mBillTitle.setText(bill.rawBill.title);
        mBillRecipient.setText("Paid by " + bill.recipientName);

        mBillAmount.setText("$" + bill.rawBill.getTotalAmount()); // TODO: Consider using BigDecimal
        mBillDescription.setText(bill.rawBill.description);
        try {
            mBillTime.setText(Helpers.shortDate(DateFormat.SHORT, bill.rawBill.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mRecycler.setAdapter(new BillDetailAmountsItemAdapter(bill.amounts));
    }

    @Override
    public void showAlert(String info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAlert(int info) {
        Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }
}
