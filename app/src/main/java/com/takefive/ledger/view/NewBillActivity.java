package com.takefive.ledger.view;

import android.animation.Animator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewBillActivity extends AppCompatActivity {

    @Bind(R.id.newBillContent)
    RelativeLayout mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);
        ButterKnife.bind(this);
        // ((MyApplication) getApplication()).inject(this);

        overridePendingTransition(0, 0);

        Intent intent = getIntent();

        if (savedInstanceState == null &&
                intent.hasExtra("revealLocation") &&
                intent.hasExtra("revealStartRadius")) {

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
        Log.d("NewBillActivity", "Animation with location x: " + location[0] + " y: " + location[1] + " ending: " + endingRadius);

        Animator anim = ViewAnimationUtils.createCircularReveal(
                mContent, location[0], location[1], 0, (float) endingRadius);
        anim.setDuration(600);
        mContent.setVisibility(View.VISIBLE);
        anim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
