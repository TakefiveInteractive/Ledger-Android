package com.takefive.ledger.midData;

import com.takefive.ledger.Helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Provider;

import java8.util.function.Function;
import zyu19.libs.action.chain.config.Producer;

/**
 * Created by zyu on 4/14/16.
 */
public class Money {

    final Locale locale;
    final Currency type;
    final Random r = new Random();

    // value includes fraction part as integer
    final long value;
    final NumberFormat formatter;

    public Money(Locale locale, long value) {
        this.type = Currency.getInstance(locale);
        this.locale = locale;
        this.formatter = NumberFormat.getCurrencyInstance(locale);
        this.value = value;
    }

    public Money(Locale locale, String value) {
        this.type = Currency.getInstance(locale);
        this.locale = locale;
        this.formatter = NumberFormat.getCurrencyInstance(locale);
        this.value = Long.parseLong(value.replaceAll("[^-\\d]", ""));
    }

    public boolean isNegative() {
        return value < 0;
    }

    public Money appendDigit(int digit) {
        if(digit > 9 || digit < 0)
            return null;
        return new Money(locale, value * 10 + digit);
    }

    public Money removeDigits(int numDigits) {
        long ans = value;
        for(int i=0; i<numDigits; i++)
            ans = ans / 10;
        return new Money(locale, ans);
    }

    // Returns a NEW Money
    public Money plus(Money m) {
        if(m.locale != locale)
            throw new IllegalStateException("Cannot operate on two currencies without conversion.");
        return new Money(locale, value + m.value);
    }

    // Returns a NEW Money
    public Money minus(Money m) {
        if(m.locale != locale)
            throw new IllegalStateException("Cannot operate on two currencies without conversion.");
        return new Money(locale, value - m.value);
    }

    @Override
    public String toString() {
        boolean negative = value < 0;
        long value = negative ? -this.value : this.value;

        long powered = 1;
        for(int i=0; i<type.getDefaultFractionDigits(); i++)
            powered = powered * 10;

        StringBuilder builder = new StringBuilder();
        if(negative)
            builder.append('-');
        builder.append(value / powered);
        builder.append('.');

        String fracString = Long.toString(value % powered);
        int needZeros = type.getDefaultFractionDigits() - fracString.length();
        for(int i=0; i<needZeros; i++)
            builder.append(0);
        builder.append(fracString);

        return Helpers.currencyText(builder.toString(), locale);
    }

    public double toDouble() {
        long powered = 1;
        for(int i=0; i<type.getDefaultFractionDigits(); i++)
            powered = powered * 10;
        return (double) value / powered;
    }

    public <K> Map<K, Money> fairSplit(Collection<K> keys) {
        long div = value / keys.size();
        if(div * keys.size() == value) {
            HashMap<K, Money> ans = new HashMap<>();
            for(K key : keys)
                ans.put(key, new Money(locale, div));
            return ans;
        }

        // Otherwise we need to randomly select one person to fill the hole
        long hole = value - (div * keys.size());
        int winner = r.nextInt(keys.size());
        HashMap<K, Money> ans = new HashMap<>();

        int i = 0;
        for(K key: keys) {
            if(i == winner)
                ans.put(key, new Money(locale, div + hole));
            else ans.put(key, new Money(locale, div));
            i++;
        }

        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Money) {
            Money m = (Money)o;
            if(m.locale == locale && m.value == value)
                return true;
            else return false;
        } else return false;
    }
}
