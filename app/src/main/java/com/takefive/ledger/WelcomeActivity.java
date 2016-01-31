package com.takefive.ledger;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.transition.Scene;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends Activity {

    @Bind(R.id.backgroundImage)
    ImageView mBgImg;

    @Bind(R.id.login)
    Button mLogin;

    @Bind(R.id.nameTag)
    TextView mNameTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        Helpers.setMargins(mNameTag, screenSize.y/3, null, null, null);
        Helpers.setMargins(mLogin, null, -getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin), null, null);
        ViewPropertyAnimator[] animators = new ViewPropertyAnimator[]{
                mLogin.animate()
                        .translationYBy(-getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin))
                        .alpha(1)
                        .setDuration(1000),
                mNameTag.animate()
                        .translationYBy(screenSize.y / 5 - screenSize.y / 3)
                        .alpha(1)
                        .setDuration(1000)
        };

        for(ViewPropertyAnimator x : animators) x.start();
    }

}
