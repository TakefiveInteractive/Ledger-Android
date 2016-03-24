package com.takefive.ledger.view;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.takefive.ledger.R;
import com.takefive.ledger.presenter.FbUserInfo;

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
    TextView mBoardName;
    @Bind(R.id.newBoardFriendsView)
    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_board, container, false);
        ButterKnife.bind(this, root);
        getDialog().setCanceledOnTouchOutside(true);
        ((MainActivity) getActivity()).presenter.loadUserFriends(info -> {
            FriendsListAdapter adapter = new FriendsListAdapter(getContext(), info);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new FriendsListAdapter(getContext(), new ArrayList<>()));

        return root;
    }

    @OnClick(R.id.closePopup)
    public void onCloseButtonClick() {
        this.dismiss();
    }

}

class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

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

        holder.mLayout.setOnClickListener(layout -> {
            holder.mCheck.setChecked(!holder.mCheck.isChecked());
            if (holder.mCheck.isChecked())
                mSelected.add(mInfoList.get(position).id);
            else
                mSelected.remove(mInfoList.get(position).id);
        });
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    public List<String> getSelected() {
        return mSelected;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mLayout;
        public CheckBox mCheck;
        public ImageView mAvatar;
        public TextView mName;

        public ViewHolder(View layout) {
            super(layout);
            mLayout = layout;
            mCheck =(CheckBox) mLayout.findViewById(R.id.friendSelect);
            mAvatar = (ImageView) mLayout.findViewById(R.id.friendAvatar);
            mName = (TextView) mLayout.findViewById(R.id.friendName);
        }
    }
}

