package com.takefive.ledger.view.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.takefive.ledger.Helpers;

/**
 * Created by @tourbillon on 4/3/16.
 */
public class MoneyEdit extends EditText {

    private boolean parsingText;
    private boolean shouldSendEvent;

    private OnAmountChangeListener listener;

    public MoneyEdit(Context context) {
        super(context);
        init();
    }

    public MoneyEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoneyEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        parsingText = false;
        shouldSendEvent = false;
        listener = amount -> {};
        this.setRawInputType(Configuration.KEYBOARD_QWERTY);
        this.addTextChangedListener(new MoneyFormatListener());
        setText("0.00");
    }

    public double getAmount() {
        try {
            return Double.valueOf(getText().toString().replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setOnAmountChangeListener(OnAmountChangeListener listener) {
        this.listener = listener;
    }

    private class MoneyEditFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                parsingText = true;
                setText(Helpers.parseText(getText().toString()));
            }
        }

    }

    private class MoneyFormatListener implements TextWatcher {

        private String previous = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            previous = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (parsingText) {
                parsingText = false;
                if (shouldSendEvent)
                    listener.onAmountChange(getAmount());
                return;
            }

            parsingText = true;
            String str;
            try {
                str = s.toString();
                Double.parseDouble(str);
                int dotIndex = str.indexOf('.');
                if (dotIndex != -1 && dotIndex != str.length() - 1 && str.substring(dotIndex + 1).length() > 2)
                    str = str.substring(0, dotIndex + 3);
                shouldSendEvent = true;
            } catch (NumberFormatException e) {
                Log.d("MoneyEdit", "New value is not a double. Falling back.");
                str = previous;
                shouldSendEvent = false;
            }

            setText(str);
            setSelection(str.length());
        }
    }

    public interface OnAmountChangeListener {
        void onAmountChange(double amount);
    }

}
