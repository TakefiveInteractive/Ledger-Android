package com.takefive.ledger.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.takefive.ledger.R;

import java.util.ArrayList;

/**
 * Created by @tourbillon on 2/6/16.
 */
public class DescriptionView extends CardView {

    private Context context;
    private AttributeSet attrs;
    private CardView mCard;

    private String title;
    private ArrayList<View> leftColumn;
    private ArrayList<View> rightColumn;

    public DescriptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public DescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public void init() {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DescriptionView, 0, 0);
        title = a.getString(R.styleable.DescriptionView_card_title);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(mCard == null){
            super.addView(child, index, params);
        } else {
            //Forward these calls to the content view
            mCard.addView(child, index, params);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LinearLayout titleLayout = new LinearLayout(context);
        super.onDraw(canvas);
    }
}
