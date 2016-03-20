package com.takefive.ledger.view.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zyu on 2/6/16.
 */
public class DotMark extends ImageView {
    Path path;
    RectF rect;
    Paint paint;

    public DotMark(Context context) {
        super(context);
        initMem();
    }

    public DotMark(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMem();
    }

    public DotMark(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMem();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DotMark(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initMem();
    }

    public void initMem() {
        path = new Path();
        rect = new RectF();
        paint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int h = getMeasuredHeight();
        int w = getMeasuredWidth();

        rect.set(0, 0, w, h);
        path.reset();
        path.addArc(rect, 0, 360);

        Drawable src = getDrawable();
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        if(src instanceof ColorDrawable)
            paint.setColor(((ColorDrawable)src).getColor());
        else paint.setColor(Color.BLUE);

        canvas.drawPath(path, paint);
    }
}
