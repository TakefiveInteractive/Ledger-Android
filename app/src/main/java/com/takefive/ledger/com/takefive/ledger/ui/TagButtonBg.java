package com.takefive.ledger.com.takefive.ledger.ui;

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
import android.view.View;
import android.widget.Button;
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
    public TagButtonBg(Context context) {
        super(context);
    }

    public TagButtonBg(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagButtonBg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagButtonBg(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getMeasuredHeight();
        int w = getMeasuredWidth();

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(w, h);
        path.lineTo(w, 0);
        path.lineTo(0, 0);

        Drawable src = getDrawable();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        if(src instanceof ColorDrawable)
            paint.setColor(((ColorDrawable)src).getColor());
        else paint.setColor(Color.BLUE);

        canvas.drawPath(path, paint);
    }
}
