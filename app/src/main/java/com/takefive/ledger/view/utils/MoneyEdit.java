package com.takefive.ledger.view.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
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

    private OnAmountChangeListener listener;

    private NumberFormat format;

    final private Locale locale;

    final private Currency currency;

    final private Money zero;

    private Money amount;

    public MoneyEdit(Context context) {
        super(context);
        locale = context.getResources().getConfiguration().locale;
        currency = Currency.getInstance(locale);
        zero = new Money(locale, 0);
        init();
    }

    public MoneyEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        locale = context.getResources().getConfiguration().locale;
        currency = Currency.getInstance(locale);
        zero = new Money(locale, 0);
        init();
    }

    public MoneyEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        locale = context.getResources().getConfiguration().locale;
        currency = Currency.getInstance(locale);
        zero = new Money(locale, 0);
        init();
    }

    private void init() {
        try {
            format = NumberFormat.getCurrencyInstance(getTextLocale());
            format.setRoundingMode(RoundingMode.DOWN);
            listener = null;
            amount = zero;

            setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            setRawInputType(Configuration.KEYBOARD_QWERTY);
            setHint(Currency.getInstance(getTextLocale()).getSymbol());
            setAmount(zero);
            addTextChangedListener(new MoneyFormatListener());
            /*
            setOnKeyListener((v, keyCode, event) -> {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_0:
                            amount = amount.appendDigit(0);
                            break;
                        case KeyEvent.KEYCODE_1:
                            amount = amount.appendDigit(1);
                            break;
                        case KeyEvent.KEYCODE_2:
                            amount = amount.appendDigit(2);
                            break;
                        case KeyEvent.KEYCODE_3:
                            amount = amount.appendDigit(3);
                            break;
                        case KeyEvent.KEYCODE_4:
                            amount = amount.appendDigit(4);
                            break;
                        case KeyEvent.KEYCODE_5:
                            amount = amount.appendDigit(5);
                            break;
                        case KeyEvent.KEYCODE_6:
                            amount = amount.appendDigit(6);
                            break;
                        case KeyEvent.KEYCODE_7:
                            amount = amount.appendDigit(7);
                            break;
                        case KeyEvent.KEYCODE_8:
                            amount = amount.appendDigit(8);
                            break;
                        case KeyEvent.KEYCODE_9:
                            amount = amount.appendDigit(9);
                            break;
                        case KeyEvent.KEYCODE_DEL:
                            amount = amount.removeDigits(1);
                            break;
                        default:
                            break;
                    }
                    setAmount(amount);
                    if (listener != null)
                        listener.onAmountChange(amount);
                }
                return true;
            });
            */
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void onSelectionChanged(int start, int end) {
        setSelection(getText().length());
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money m) {
        amount = m;
        String newStr = m.toString();
        setText(newStr);
        setSelection(newStr.length());
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
            removeTextChangedListener(this);

            try {
                Money newAmount = new Money(locale, s.toString());
                boolean amountChange = !newAmount.equals(amount);
                amount = newAmount;

                String newStr = newAmount.toString();
                setText(newStr);
                setSelection(newStr.length());

                if (amountChange && listener != null) {
                    amount = newAmount;
                    listener.onAmountChange(newAmount);
                }
            } catch (NumberFormatException err) {
                err.printStackTrace();
                // make sure we use the last valid value.
                setAmount(amount);
            }

            addTextChangedListener(this);
        }
    }

    public interface OnAmountChangeListener {
        void onAmountChange(Money amount);
    }

}
