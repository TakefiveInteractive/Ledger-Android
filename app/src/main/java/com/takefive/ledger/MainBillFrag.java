package com.takefive.ledger;

import android.animation.Animator;
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
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.takefive.ledger.ui.NamedFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    ArrayList<MainBillData> mListData = new ArrayList<>();
    ArrayAdapter mListAdapter;
    @Bind(R.id.shadow)
    View mShadow;
    @Bind(R.id.popupCard)
    CardView mPopup;
    @Bind(R.id.closePopup)
    BootstrapCircleThumbnail mClosePopup;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_main_bill, container, false);
        ButterKnife.bind(this, root);

        initTransparentPopup();

        // init list
        mListAdapter = new MainBillAdapter(getContext(), mListData);
        mList.setAdapter(mListAdapter);
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            if (bPopupShown) return;
            updatePopup((MainBillData) mList.getItemAtPosition(position));
            showPopup();
        });

        // Add data.
        try {
            SimpleDateFormat dater = new SimpleDateFormat("dd/MM/yy HH:mm");
            mListData.add(new MainBillData("zak", 12.22f, "Cravings", "Collected $24", dater.parse("02/12/15 12:50"), "A had orange chicken, B ordered fried rice."));
            mListData.add(new MainBillData(null, 16.12f, "Circle K", "Collected $123", new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(3)), "Water water water"));
            mListData.add(new MainBillData("mary", 56.22f, "Amazon", "Collected $12.22", new Date(new Date().getTime() - TimeUnit.HOURS.toMillis(1)), "We bought some xxx phone with xxx sim card"));
            mListAdapter.notifyDataSetChanged();
        } catch (ParseException err) {
            err.printStackTrace();
        }

        return root;
    }

    @OnClick(R.id.shadow)
    void clickShadow() {
        hidePopup();
    }

    @OnClick(R.id.closePopup)
    void clickClosePopup() {hidePopup();}

    //----- Popup: Update content
    @Bind(R.id.billDescription)
    TextView mBillDesc;
    @Bind(R.id.billAmount)
    TextView mBillAmount;
    @Bind(R.id.billTime)
    TextView mBillTime;

    void updatePopup(MainBillData data) {
        mBillDesc.setText(data.detailDesc);
        mBillAmount.setText("S" + data.paidAmount);
        mBillTime.setText(Helpers.longDate(DateFormat.MEDIUM, data.time));
    }

    //----- Popup: Animation

    boolean bPopupShown = false;

    private void initTransparentPopup() {
        mShadow.setClickable(false);
        mShadow.setFocusable(false);
        mPopup.setClickable(false);
        mPopup.setFocusable(false);
        mClosePopup.setClickable(false);
        mClosePopup.setFocusable(false);
        //mPopup.setVisibility(View.GONE);
        mShadow.setAlpha(0);
        mPopup.setAlpha(0);
        bPopupShown = false;
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
                .setDuration(666)
        };
        for(ViewPropertyAnimator animator : animators)
            animator.start();

        bPopupShown = true;
    }
}

class MainBillData {
    public MainBillData(String a, float b, String c, String d, Date e, String _detailDesc) {
        whoPaid = a;
        paidAmount = b;
        desc1 = c;
        desc2 = d;
        time = e;
        detailDesc = _detailDesc;
    }
    String whoPaid;
    float paidAmount;
    String desc1, desc2, detailDesc;
    Date time;
}

class MainBillAdapter extends ArrayAdapter<MainBillData> {

    public MainBillAdapter(Context context, List<MainBillData> objects) {
        super(context, R.layout.item_bill_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bill_list, parent, false);

        MainBillData data = getItem(position);

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

        return convertView;
    }
}
