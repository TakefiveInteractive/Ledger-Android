package com.takefive.ledger.view.utils;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.util.HashSet;

/**
 * Created by zyu on 6/12/16.
 */
public class NiceTextEdit extends EditText {
    public NiceTextEdit(Context context) {
        super(context);
    }

    public NiceTextEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NiceTextEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    HashSet<TextWatcher> textWatchers = new HashSet<>();

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
        textWatchers.add(watcher);
    }

    public void clearAllTextChangedListeners() {
        for(TextWatcher watcher : textWatchers)
            removeTextChangedListener(watcher);
        textWatchers.clear();
    }
}
