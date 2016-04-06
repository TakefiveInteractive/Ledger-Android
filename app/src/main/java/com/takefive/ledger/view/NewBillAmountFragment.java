package com.takefive.ledger.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.R;
import com.takefive.ledger.midData.ledger.RawPerson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBillAmountFragment extends Fragment {

    @Bind(R.id.newBillMembersRecyclerView)
    RecyclerView mRecyclerView;

    private MembersSelectionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_bill_amount, container, false);
        ButterKnife.bind(this, root);

        adapter = new MembersSelectionAdapter(getContext(), new ArrayList<>());
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(adapter);

        return root;
    }
    
    private class MembersSelectionAdapter extends RecyclerView.Adapter<MembersSelectionAdapter.ViewHolder> {

        private Context mContext;
        private List<RawPerson> mInfoList;

        public MembersSelectionAdapter(Context context, List<RawPerson> infoList) {
            mContext = context;
            mInfoList = infoList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_person_selection, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 0) {
                holder.mAvatar.setVisibility(View.GONE);
                holder.mName.setVisibility(View.GONE);
                holder.mNewPerson.setOnClickListener(v -> {
                    NewBillMembersFragment fragment = new NewBillMembersFragment();
                    fragment.setOnSelectionCompleteListener(selection -> {
                        mInfoList = selection;
                        notifyDataSetChanged();
                    });
                    fragment.show(getFragmentManager(), "fragment_new_bill_members");
                });
                holder.mNewPerson.setVisibility(View.VISIBLE);
            } else {
                RawPerson person = mInfoList.get(position);
                holder.mName.setText(person.name);
                Picasso.with(mContext).load(person.avatarUrl).into(holder.mAvatar);
            }
        }

        @Override
        public int getItemCount() {
            return mInfoList.size() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView mNewPerson;
            BootstrapCircleThumbnail mAvatar;
            TextView mName;

            public ViewHolder(View itemView) {
                super(itemView);
                mNewPerson = (ImageView) itemView.findViewById(R.id.personAdd);
                mAvatar = (BootstrapCircleThumbnail) itemView.findViewById(R.id.personAvatar);
                mName = (TextView) itemView.findViewById(R.id.personName);
            }

        }

    }
}