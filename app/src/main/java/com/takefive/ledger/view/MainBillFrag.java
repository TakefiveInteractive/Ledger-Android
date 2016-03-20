package com.takefive.ledger.view;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.takefive.ledger.Helpers;
import com.takefive.ledger.R;
import com.takefive.ledger.presenter.client.LedgerService;
import com.takefive.ledger.model.RawBill;
import com.takefive.ledger.model.RawBoard;
import com.takefive.ledger.view.utils.NamedFragment;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBillFrag extends NamedFragment {

    @Bind(R.id.billList)
    ListView mList;
    ArrayList<RawBill> mListData = new ArrayList<>();
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
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            if (bPopupShown) return;
            updatePopup((RawBill) mList.getItemAtPosition(position));
            showPopup();
        });

        return root;
    }

    public void showBillsList(List<RawBill> bills) {
        MainBillAdapter adapter = new MainBillAdapter(getContext(), bills);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    void updatePopup(RawBill data) {
        mBillDesc.setText(data.description);
        mBillAmount.setText("S" + data.getTotalAmount());
        try {
            mBillTime.setText(Helpers.longDate(DateFormat.MEDIUM, data.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    class MainBillAdapter extends ArrayAdapter<RawBill> {

        public MainBillAdapter(Context context, List<RawBill> objects) {
            super(context, R.layout.item_bill_list, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bill_list, parent, false);

            RawBill data = getItem(position);

            TextView whoPaid = ButterKnife.findById(convertView, R.id.payer);
            TextView paidAmount = ButterKnife.findById(convertView, R.id.paidAmount);
            TextView desc1 = ButterKnife.findById(convertView, R.id.desc1);
            TextView desc2 = ButterKnife.findById(convertView, R.id.desc2);
            AwesomeTextView desc2icon = ButterKnife.findById(convertView, R.id.desc2icon);
            TextView time = ButterKnife.findById(convertView, R.id.time);

            // TODO: use userid here rather than user name
            whoPaid.setText(data.recipient.equals(getActivity().getIntent().getStringExtra("username")) ? "You paid:" : data.recipient + " paid:");
            paidAmount.setText("$" + data.getTotalAmount());
            desc1.setText(data.title);

            desc2icon.setBootstrapText(new BootstrapText.Builder(getContext())
                    .addFontAwesomeIcon(FontAwesome.FA_CREDIT_CARD).build());
            desc2.setText(" " + data.description);

            try {
                time.setText(Helpers.shortDate(DateFormat.SHORT, data.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
