package com.takefive.ledger;

import android.animation.Animator;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.takefive.ledger.ui.NamedFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBillFrag extends NamedFragment {

    @Bind(R.id.billList)
    ListView mList;
    @Bind(R.id.shadow)
    View mShadow;
    @Bind(R.id.popupCard)
    CardView mPopup;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_main_bill, container, false);
        ButterKnife.bind(this, root);

        initTransparentPopup();

        try {
            SimpleDateFormat dater = new SimpleDateFormat("dd/MM/yy HH:mm");
            mList.setAdapter(new SimpleAdapter(getContext(), Arrays.asList(
                    new Data("zak", 12.22f, "Cravings", "Collected $24", dater.parse("02/12/15 12:50")),
                    new Data(null, 16.12f, "Circle K", "Collected $123", new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(3))),
                    new Data("mary", 56.22f, "Amazon", "Collected $12.22", new Date(new Date().getTime() - TimeUnit.HOURS.toMillis(1)))
            )));
        } catch (ParseException err) {
            err.printStackTrace();
        }

        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            if(bPopupShown) return;
            //Object data = mList.getItemAtPosition(position);
            showPopup();
        });

        return root;
    }

    boolean bPopupShown = false;

    private void initTransparentPopup() {
        mShadow.setClickable(false);
        mShadow.setFocusable(false);
        //mPopup.setVisibility(View.GONE);
        mShadow.setAlpha(0);
        mPopup.setAlpha(0);
        bPopupShown = false;
    }

    @OnClick(R.id.shadow)
    void clickShadow() {
        hidePopup();
    }

    private void hidePopup() {
        if(!bPopupShown) return;
        int popupHeight = mPopup.getHeight();

        Point screenSize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);

        ViewPropertyAnimator[] animators = new ViewPropertyAnimator[] {
                mShadow.animate().alpha(0)
                        .setDuration(666).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mShadow.setClickable(false);
                        mShadow.setFocusable(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }),
                mPopup.animate().alpha(0.5f).translationYBy(popupHeight)
                        .setDuration(666)
        };
        for(ViewPropertyAnimator animator : animators)
            animator.start();
        mShadow.setClickable(false);
        bPopupShown = false;
    }

    // showPopup relies on mPopup to report actual height, so
    //     we must set its content values before showPopup()
    private void showPopup() {
        if(bPopupShown) return;
        int popupHeight = mPopup.getHeight();

        initTransparentPopup();
        Point screenSize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(screenSize);
        mPopup.setTranslationY(popupHeight);
        mShadow.setVisibility(View.VISIBLE);
        mPopup.setVisibility(View.VISIBLE);
        mPopup.setAlpha(0.5f);

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.alpha_fgshade_with_popup, outValue, true);
        float alpha_fgshade_with_popup = outValue.getFloat();

        ViewPropertyAnimator[] animators = new ViewPropertyAnimator[] {
                mShadow.animate().alpha(alpha_fgshade_with_popup)
                .setDuration(666).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mShadow.setClickable(true);
                        mShadow.setFocusable(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }),
                mPopup.animate().alpha(1).translationYBy(-popupHeight)
                .setDuration(666)
        };
        for(ViewPropertyAnimator animator : animators)
            animator.start();

        bPopupShown = true;
    }
}

class Data {
    public Data(String a, float b, String c, String d, Date e) {
        whoPaid = a;
        paidAmount = b;
        desc1 = c;
        desc2 = d;
        time = e;
    }
    String whoPaid;
    float paidAmount;
    String desc1, desc2;
    Date time;
}

class SimpleAdapter extends ArrayAdapter<Data> {
    public SimpleAdapter(Context context) {
        super(context, R.layout.item_main);
    }

    public SimpleAdapter(Context context, List<Data> objects) {
        super(context, R.layout.item_main, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_main, parent, false);

        Data data = getItem(position);

        TextView whoPaid = ButterKnife.findById(convertView, R.id.payer);
        TextView paidAmount = ButterKnife.findById(convertView, R.id.paidAmount);
        TextView desc1 = ButterKnife.findById(convertView, R.id.desc1);
        TextView desc2 = ButterKnife.findById(convertView, R.id.desc2);
        AwesomeTextView desc2icon = ButterKnife.findById(convertView, R.id.desc2icon);
        TextView time = ButterKnife.findById(convertView, R.id.time);

        whoPaid.setText(data.whoPaid == null ? "You paid:" : data.whoPaid + " paid:");
        paidAmount.setText("$" + data.paidAmount);
        desc1.setText(data.desc1);

        desc2icon.setBootstrapText(new BootstrapText.Builder(getContext())
                .addFontAwesomeIcon(FontAwesome.FA_CREDIT_CARD).build());
        desc2.setText(" " + data.desc2);

        time.setText(Helpers.shortDate(DateFormat.SHORT, data.time));

        convertView.setOnClickListener((View view) -> {
            Intent intent = new Intent(view.getContext(), BillDetailActivity.class);
            view.getContext().startActivity(intent);
        });

        return convertView;
    }
}
