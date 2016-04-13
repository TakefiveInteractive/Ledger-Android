package com.takefive.ledger.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.IPresenter;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.fb.BusinessFbLoginResult;
import com.takefive.ledger.midData.ledger.NewBoardRequest;
import com.takefive.ledger.midData.fb.FbUserInfo;
import com.takefive.ledger.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBoardFragment extends DialogFragment {

    @Bind(R.id.newBoardName)
    EditText mBoardName;
    @Bind(R.id.newBoardFriendsView)
    RecyclerView mRecyclerView;

    FriendsListAdapter adapter;

    IMainView mMainView;
    MainPresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_board, container, false);
        ButterKnife.bind(this, root);
        adapter = new FriendsListAdapter(getContext(), new ArrayList<>());
        getDialog().setCanceledOnTouchOutside(true);

        BusinessFbLoginResult fbLoginResult = new BusinessFbLoginResult();
        fbLoginResult.setToken(AccessToken.getCurrentAccessToken());

        // find mainView and mainPresenter
        mMainView = (IMainView) getActivity();
        mPresenter = ((MainActivity)getActivity()).presenter;

        mPresenter.loadUserFriends(fbLoginResult, info -> {
            adapter = new FriendsListAdapter(getContext(), info);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        return root;
    }

    @OnClick(R.id.newBoardSubmit)
    public void createBoard() {
        if (mBoardName.getText().toString().length() == 0) {
            mMainView.showAlert(R.string.new_board_no_name);
            mBoardName.requestFocus();
            return;
        } else if (adapter.getSelected().size() == 0) {
            mMainView.showAlert(R.string.new_board_no_friends_set);
            return;
        }
        NewBoardRequest request = new NewBoardRequest();
        request.name = mBoardName.getText().toString();
        request.isActive = true;
        request.members = new ArrayList<>();
        request.members_fromfb = adapter.getSelected();
        mPresenter.createBoard(request, r -> {
            if (r.isSuccessful()) {
                mMainView.showAlert(R.string.new_board_success);
            } else {
                Log.e("create board", String.format("code: %d, response: %s", r.code(), r.errorBody().string()));
                mMainView.showAlert(R.string.network_failure);
            }
            this.dismiss();
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), R.style.MyDialogTheme);
        Window window = dialog.getWindow();
        // window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

        Context mContext;
        List<FbUserInfo> mInfoList;
        List<String> mSelected;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friends_list, parent, false);
            return new ViewHolder(v);
        }

        public FriendsListAdapter(Context context, List<FbUserInfo> infoList) {
            mContext = context;
            mInfoList = infoList;
            mSelected = new ArrayList<>();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            FbUserInfo info = mInfoList.get(position);
            if (info.id == null || info.avatarUrl == null || info.name == null)
                return;
            holder.mName.setText(mInfoList.get(position).name);
            Picasso.with(mContext).load(info.avatarUrl).into(holder.mAvatar);

            holder.mCheck.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked)
                    mSelected.add(mInfoList.get(position).id);
                else
                    mSelected.remove(mInfoList.get(position).id);
            });
            holder.mLayout.setOnClickListener(layout -> holder.mCheck.setChecked(!holder.mCheck.isChecked()));
        }

        @Override
        public int getItemCount() {
            return mInfoList.size();
        }

        public List<String> getSelected() {
            return mSelected;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mLayout;
            public CheckBox mCheck;
            public ImageView mAvatar;
            public TextView mName;

            public ViewHolder(View layout) {
                super(layout);
                mLayout = layout;
                mCheck = (CheckBox) mLayout.findViewById(R.id.friendSelect);
                mAvatar = (ImageView) mLayout.findViewById(R.id.friendAvatar);
                mName = (TextView) mLayout.findViewById(R.id.friendName);
            }
        }
    }

}
