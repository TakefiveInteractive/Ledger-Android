package com.takefive.ledger.view.utils;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.takefive.ledger.R;

/**
 * Created by @tourbillon on 3/21/16.
 */
public class PopupCardView extends RelativeLayout {

    View mShadow;
    CardView mPopup;
    BootstrapCircleThumbnail mClosePopup;
    LinearLayout mContent;
    RelativeLayout mRoot;

    private boolean shown;

    public PopupCardView(Context context) {
        super(context);
        bind();
        init();
    }

    public PopupCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bind();
        init();
    }

    public PopupCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind();
        init();
    }

    private void bind() {
        inflate(getContext(), R.layout.popup_card_view, this);
        mShadow = findViewById(R.id.popupCardShadow);
        mPopup = (CardView) findViewById(R.id.popupCard);
        mClosePopup = (BootstrapCircleThumbnail) findViewById(R.id.closePopupCard);
        mContent = (LinearLayout) findViewById(R.id.popupCardContent);
        mRoot = (RelativeLayout) findViewById(R.id.popupCardRoot);
    }

    public void init() {
        shown = false;
        mClosePopup.setOnClickListener(v -> closePopup());
        mShadow.setOnClickListener(v -> closePopup());
        mShadow.setClickable(false);
        mShadow.setFocusable(false);
        mPopup.setClickable(false);
        mPopup.setFocusable(false);
        mClosePopup.setClickable(false);
        mClosePopup.setFocusable(false);
        mShadow.setAlpha(0);
        mPopup.setAlpha(0);
        mRoot.setVisibility(View.GONE);
    }

    public void showPopup() {
        if (shown) return;
        init();
        mRoot.setVisibility(View.VISIBLE);
        this.bringToFront();

        int popupHeight = mPopup.getHeight();
        Point screenSize = new Point();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(screenSize);
        mPopup.setTranslationY(popupHeight);
        mPopup.setAlpha(0.5f);

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.alpha_fgshade_with_popup, outValue, true);
        float alpha_fgshade_with_popup = outValue.getFloat();

        ViewPropertyAnimator[] animators = new ViewPropertyAnimator[]{
                mShadow.animate().alpha(alpha_fgshade_with_popup)
                        .setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mShadow.setClickable(true);
                        mShadow.setFocusable(true);
                        mPopup.setClickable(true);
                        mPopup.setFocusable(true);
                        mClosePopup.setClickable(true);
                        mClosePopup.setFocusable(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }),
                mPopup.animate().alpha(1).translationYBy(-popupHeight)
                        .setDuration(500)
        };
        for (ViewPropertyAnimator animator : animators)
            animator.start();

        shown = true;
    }

    public void closePopup() {
        if (!shown) return;
        int popupHeight = mPopup.getHeight();

        Point screenSize = new Point();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getSize(screenSize);

        ViewPropertyAnimator[] animators = new ViewPropertyAnimator[]{
                mShadow.animate().alpha(0)
                        .setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mShadow.setClickable(false);
                        mShadow.setFocusable(false);
                        mPopup.setClickable(false);
                        mPopup.setFocusable(false);
                        mClosePopup.setClickable(false);
                        mClosePopup.setFocusable(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }),
                mPopup.animate().alpha(0.5f).translationYBy(popupHeight)
                        .setDuration(500)
        };
        for (ViewPropertyAnimator animator : animators)
            animator.start();
        mShadow.setClickable(false);
        mRoot.setVisibility(View.GONE);
        shown = false;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mContent == null)
            super.addView(child, index, params);
        else
            mContent.addView(child, index, params);
    }

    public boolean isPopped() {
        return shown;
    }
}
