package com.takefive.ledger.view.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.takefive.ledger.Helpers;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * Created by @tourbillon on 4/3/16.
 */
public class MoneyEdit extends EditText {

    private boolean parsingText;

    private OnAmountChangeListener listener;

    private NumberFormat format;

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
        format = NumberFormat.getCurrencyInstance(getTextLocale());
        format.setRoundingMode(RoundingMode.DOWN);
        listener = amount -> {};
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        setRawInputType(Configuration.KEYBOARD_QWERTY);
        addTextChangedListener(new MoneyFormatListener());
        setHint(Currency.getInstance(getTextLocale()).getSymbol());
        setText("0.00");
    }

    @Override
    public void onSelectionChanged(int start, int end) {
        setSelection(getText().length());
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
                listener.onAmountChange(getAmount());
                return;
            }

            parsingText = true;
            String str = s.toString().replaceAll("[^\\d.]", "");
            String newStr;

            int dotIndex = str.indexOf('.');
            if (dotIndex != -1) {
                if (dotIndex >= str.length() - 2)
                    newStr = str.substring(0, dotIndex - 1) + "." + str.substring(dotIndex - 1, dotIndex) + str.substring(dotIndex + 1);
                else {
                    if (str.charAt(0) == '0')
                        newStr = "";
                    else
                        newStr = str.substring(0, dotIndex);
                    newStr += str.substring(dotIndex + 1, dotIndex + 2) + "." + str.substring(dotIndex + 2);
                }
            } else {
                Log.d("Formatting", "Mysterious condition where there isn't a dot");
                newStr = "0.00";
            }

            String formatted = format.format(Double.valueOf(newStr));
            setText(formatted);
            setSelection(formatted.length());
        }
    }

    public interface OnAmountChangeListener {
        void onAmountChange(double amount);
    }

}
