package com.takefive.ledger;

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
import com.takefive.ledger.client.LedgerService;
import com.takefive.ledger.client.raw.DidGetBillById;
import com.takefive.ledger.client.raw.DidGetBoard;
import com.takefive.ledger.client.raw.DidGetBoardById;
import com.takefive.ledger.ui.MyFragment;
import com.takefive.ledger.ui.NamedFragment;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBillFrag extends MyFragment {

    @Bind(R.id.billList)
    ListView mList;
    ArrayList<DidGetBillById> mListData = new ArrayList<>();
    ArrayAdapter mListAdapter;
    @Bind(R.id.shadow)
    View mShadow;
    @Bind(R.id.popupCard)
    CardView mPopup;
    @Bind(R.id.closePopup)
    BootstrapCircleThumbnail mClosePopup;

    @Inject
    ActionChainFactory chainFactory;

    @Inject
    LedgerService service;


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
            updatePopup((DidGetBillById) mList.getItemAtPosition(position));
            showPopup();
        });


        // SimpleDateFormat dater = new SimpleDateFormat("dd/MM/yy HH:mm");
        // mListData.add(new DidGetBillById("zak", 12.22f, "Cravings", "Collected $24", dater.parse("02/12/15 12:50"), "A had orange chicken, B ordered fried rice."));
        // mListAdapter.notifyDataSetChanged();

        return root;
    }

    @Override
    public void onUpdate() {
        // Add data.
        chainFactory.get(errorHolder -> {
            showInfo("Cannot get boards: " + errorHolder.getCause().toString());
            errorHolder.getCause().printStackTrace();
        }).uiThen(() -> getActivity().getIntent().getStringExtra("CurrentBoardId")
        ).netConsume((String boardId) -> {
            Response<DidGetBoardById> resp = service.getBoardById(boardId).execute();
            if (!resp.isSuccessful()) {
                String msg = resp.errorBody().string();
                resp.errorBody().close();
                throw new IOException(msg);
            }
            mListData.clear();
            mListData.addAll(resp.body().bills);
        }).uiConsume(obj -> {
            mListAdapter.notifyDataSetChanged();
        }).start();
    }

    public void showInfo(String info) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    public void showInfo(int info) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
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

    void updatePopup(DidGetBillById data) {
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
}

class MainBillAdapter extends ArrayAdapter<DidGetBillById> {

    public MainBillAdapter(Context context, List<DidGetBillById> objects) {
        super(context, R.layout.item_bill_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bill_list, parent, false);

        DidGetBillById data = getItem(position);

        TextView whoPaid = ButterKnife.findById(convertView, R.id.payer);
        TextView paidAmount = ButterKnife.findById(convertView, R.id.paidAmount);
        TextView desc1 = ButterKnife.findById(convertView, R.id.desc1);
        TextView desc2 = ButterKnife.findById(convertView, R.id.desc2);
        AwesomeTextView desc2icon = ButterKnife.findById(convertView, R.id.desc2icon);
        TextView time = ButterKnife.findById(convertView, R.id.time);

        whoPaid.setText(data.recipient == null ? "You paid:" : data.recipient + " paid:");
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
