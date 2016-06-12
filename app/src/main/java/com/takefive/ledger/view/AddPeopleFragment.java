package com.takefive.ledger.view;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.fb.BusinessFbLoginResult;
import com.takefive.ledger.midData.ledger.NewBoardRequest;
import com.takefive.ledger.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java8.util.stream.StreamSupport;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPeopleFragment extends DialogFragment {

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    TheAdapter theAdapter;
    IMainView mMainView;
    MainPresenter mPresenter;

    ArrayList<String> mData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_people, container, false);
        ButterKnife.bind(this, root);
        theAdapter = new TheAdapter(getContext(), mData);
        getDialog().setCanceledOnTouchOutside(false);

        // find mainView and mainPresenter
        mMainView = (IMainView) getActivity();
        mPresenter = ((MainActivity) getActivity()).presenter;

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(theAdapter);

        return root;
    }

    @OnClick(R.id.submit)
    public void submit() {
        if (mData.size() == 0)
            this.dismiss();

        if (StreamSupport.stream(mData).anyMatch(String::isEmpty)) {
            showAlert("Please delete empty blanks");
            return;
        }

        // TODO: call presenter here to add people to DB.
    }

    @OnClick(R.id.cancel)
    public void close() {
        this.dismiss();
    }

    @OnClick(R.id.addPeople)
    public void addPeople() {
        mData.add("");
        theAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), R.style.MyFullscreenDialogTheme);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private class TheAdapter extends RecyclerView.Adapter<TheAdapter.ViewHolder> {

        Context mContext;
        List<String> mNameList;
        List<String> mSelected;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_person, parent, false);
            return new ViewHolder(v);
        }

        public TheAdapter(Context context, List<String> infoList) {
            mContext = context;
            mNameList = infoList;
            mSelected = new ArrayList<>();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mName.setText(mNameList.get(position));
            holder.mName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String name = s.toString();
                    mNameList.set(position, name);
                }
            });
            holder.mRemoveItem.setOnClickListener(view -> {
                mNameList.remove(position);
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return mNameList.size();
        }

        public List<String> getSelected() {
            return mSelected;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mLayout;
            public EditText mName;
            public Button mRemoveItem;

            public ViewHolder(View layout) {
                super(layout);
                mLayout = layout;
                mName = ButterKnife.findById(mLayout, R.id.friendName);
                mRemoveItem = ButterKnife.findById(mLayout, R.id.removeItem);
            }
        }
    }

    public void showAlert(String info) {
        Snackbar.make(getView(), info, Snackbar.LENGTH_SHORT).show();
    }

    public void showAlert(int info) {
        Snackbar.make(getView(), info, Snackbar.LENGTH_SHORT).show();
    }

}
