package com.takefive.ledger;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.takefive.ledger.ui.NamedFragment;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by zyu on 2/3/16.
 */
public class MainBalanceFrag extends NamedFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_main_balance, container, false);
    }
}
