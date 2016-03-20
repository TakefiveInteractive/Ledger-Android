package com.takefive.ledger.view.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zyu on 2/1/16.
 *
 * This simply draws a triangle to FILL the given space
 *
 * |\|||
 * | \||
 * |  \|
 */
public class TagButtonBg extends ImageView {
    Path path;
    Paint paint;

    public TagButtonBg(Context context) {
        super(context);
        initMem();
    }

    public TagButtonBg(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMem();
    }

    public TagButtonBg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMem();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagButtonBg(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initMem();
    }

    public void initMem() {
        path = new Path();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getMeasuredHeight();
        int w = getMeasuredWidth();

        path.reset();
        path.moveTo(0, 0);
        path.lineTo(w, h);
        path.lineTo(w, 0);
        path.lineTo(0, 0);

        Drawable src = getDrawable();
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        if(src instanceof ColorDrawable)
            paint.setColor(((ColorDrawable)src).getColor());
        else paint.setColor(Color.BLUE);

        canvas.drawPath(path, paint);
    }
}