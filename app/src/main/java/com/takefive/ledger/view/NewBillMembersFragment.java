package com.takefive.ledger.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.midData.ledger.RawPerson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBillMembersFragment extends DialogFragment {

    @Bind(R.id.newBillMembersView)
    RecyclerView mRecyclerView;

    @Inject
    UserStore userStore;

    private NewBillMembersAdapter adapter;

    private OnSelectionCompleteListener listener;

    private List<String> selection;

    public NewBillMembersFragment() {
        this.selection = null;
    }

    public interface OnSelectionCompleteListener {
        void onSelectionComplete(List<RawPerson> selection);
    }

    public void setSelection(List<String> selection) {
        this.selection = selection;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).inject(this);
        View root = inflater.inflate(R.layout.fragment_new_bill_members, container, false);
        ButterKnife.bind(this, root);

        adapter = new NewBillMembersAdapter(getContext(), new ArrayList<>(), selection);
        getDialog().setCanceledOnTouchOutside(true);
        ((NewBillActivity) getActivity()).presenter.loadBoardMembers((List<RawPerson> members) -> {

            adapter = new NewBillMembersAdapter(getContext(), members, selection);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        return root;
    }

    @OnClick(R.id.newBillMembersSubmit)
    public void onSubmit() {
        listener.onSelectionComplete(adapter.getSelected());
        this.dismiss();
    }

    @OnClick(R.id.newBillMembersCancel)
    public void onCancel() {
        this.dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setOnSelectionCompleteListener(OnSelectionCompleteListener listener) {
        this.listener = listener;
    }

    class NewBillMembersAdapter extends RecyclerView.Adapter<NewBillMembersAdapter.ViewHolder> {

        Context mContext;
        List<RawPerson> mInfoList;
        Map<String, Integer> mSelected;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_members_list, parent, false);
            return new ViewHolder(v);
        }

        public NewBillMembersAdapter(Context context, List<RawPerson> infoList, List<String> selected) {
            mContext = context;
            mInfoList = infoList;
            mSelected = new HashMap<>();

            for (String selectionId : selected) {
                mSelected.put(selectionId, -1);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RawPerson person = mInfoList.get(position);
            if (person._id == null || person.avatarUrl == null || person.name == null)
                return;
            holder.mName.setText(person.name);
            Picasso.with(mContext).load(person.avatarUrl).into(holder.mAvatar);
            if (mSelected.containsKey(person._id))
                mSelected.put(person._id, position);
            holder.mCheck.setChecked(mSelected.containsKey(person._id));
            holder.mCheck.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked)
                    mSelected.put(person._id, position);
                else
                    mSelected.remove(person._id);
            });
            holder.mLayout.setOnClickListener(layout -> holder.mCheck.setChecked(!holder.mCheck.isChecked()));
        }

        @Override
        public int getItemCount() {
            return mInfoList.size();
        }

        public List<RawPerson> getSelected() {
            List<RawPerson> retval = new ArrayList<>();
            for (Integer index : mSelected.values()) {
                retval.add(mInfoList.get(index));
            }
            return retval;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mLayout;
            public CheckBox mCheck;
            public ImageView mAvatar;
            public TextView mName;

            public ViewHolder(View layout) {
                super(layout);
                mLayout = layout;
                mCheck = (CheckBox) mLayout.findViewById(R.id.memberSelect);
                mAvatar = (ImageView) mLayout.findViewById(R.id.memberAvatar);
                mName = (TextView) mLayout.findViewById(R.id.memberName);
            }
        }
    }

}