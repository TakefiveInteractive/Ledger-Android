package com.takefive.ledger.view.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by @tourbillon on 4/3/16.
 */
public class MoneyEdit extends EditText {

    private boolean parsingText;

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
        this.setRawInputType(Configuration.KEYBOARD_QWERTY);
        this.addTextChangedListener(new MoneyFormatListener());
    }

    public double getAmount() {
        try {
            return Double.valueOf(getText().toString());
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private class MoneyFormatListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (parsingText) {
                parsingText = false;
                return;
            }

            parsingText = true;
            String current = s.toString().replaceAll("[^\\d.]", "");
            int dotIndex = current.indexOf('.');
            if (dotIndex != -1 && dotIndex < current.length() - 1) {
                String integer = current.substring(0, dotIndex);
                String decimal = current.substring(dotIndex + 1).replaceAll("[^\\d]", "");
                int offset = decimal.length() > 2 ? 2 : decimal.length();
                decimal = decimal.substring(0, offset);
                current = integer + "." + decimal;
            }
            setText(current);
            setSelection(current.length());
        }
    }

}
