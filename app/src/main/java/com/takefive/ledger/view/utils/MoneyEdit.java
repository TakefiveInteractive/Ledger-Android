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
import com.takefive.ledger.midData.Money;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by @tourbillon on 4/3/16.
 */
public class MoneyEdit extends EditText {

    private boolean parsingText;

    private OnAmountChangeListener listener;

    private NumberFormat format;

    final private Locale locale;

    final private Currency currency;

    final private Money zero;

    public MoneyEdit(Context context) {
        super(context);
        init();
        locale = context.getResources().getConfiguration().locale;
        currency = Currency.getInstance(locale);
        zero = new Money(locale, 0);
    }

    public MoneyEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        locale = context.getResources().getConfiguration().locale;
        currency = Currency.getInstance(locale);
        zero = new Money(locale, 0);
    }

    public MoneyEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        locale = context.getResources().getConfiguration().locale;
        currency = Currency.getInstance(locale);
        zero = new Money(locale, 0);
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

    public Money getAmount() {
        try {
            Money ans = new Money(locale, getText().toString());
            return ans;
        } catch (Exception e) {
            e.printStackTrace();
            return zero;
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
        void onAmountChange(Money amount);
    }

}
