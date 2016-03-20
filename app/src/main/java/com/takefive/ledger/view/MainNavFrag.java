package com.takefive.ledger.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.takefive.ledger.Helpers;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.model.RawBoard;
import com.takefive.ledger.model.RawMyBoards;
import com.takefive.ledger.model.RawPerson;
import com.takefive.ledger.presenter.client.LedgerService;
import com.takefive.ledger.presenter.database.RealmAccess;
import com.takefive.ledger.presenter.database.UserStore;
import com.takefive.ledger.model.db.Person;
import com.takefive.ledger.view.utils.DotMark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Response;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 2/13/16.
 */
public class MainNavFrag extends Fragment {
    @Bind(R.id.profile_name_text)
    TextView mUserName;
    @Bind(R.id.chosen_account_view)
    FrameLayout mSideImgLayout;
    @Bind(R.id.chosen_account_content_view)
    RelativeLayout mSideImgContent;
    @Bind(R.id.boardList)
    ListView mList;
    ArrayList<RawMyBoards.Entry> mListData = new ArrayList<>();
    MainNavAdapter mListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.drawer_main, container, false);
        ButterKnife.bind(this, root);
        ((MyApplication) getActivity().getApplication()).inject(this);

        // Fix drawer location after enabling status bar transparency.
        // Lollipop <=> v21, corresponding with values-v21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.LayoutParams p = mSideImgLayout.getLayoutParams();
            p.height += Helpers.getStatusBarHeight(getResources());
            mSideImgLayout.setLayoutParams(p);

            Helpers.setMargins(mSideImgContent, Helpers.getStatusBarHeight(getResources()), null, null, null);
        }

        // TODO: provide a graphical interface to refresh (That logic is ready, though)

        // init list
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            onClickItem(position);
        });

        MainActivity mainActivity = ((MainActivity) getActivity());
        mainActivity.presenter.loadMyUserInfo();
        mainActivity.presenter.loadMyBoards();
        return root;
    }

    public void showMyUserInfo(RawPerson me) {
        mUserName.setText(me.name);
    }

    void showMyBoards(RawMyBoards myBoards) {
        ArrayAdapter adapter = new MainNavAdapter(getContext(), myBoards.boards);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    void onClickItem(int pos) {
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.presenter.loadBills(mListAdapter.getItem(pos).id);
    }

    class MainNavAdapter extends ArrayAdapter<RawMyBoards.Entry> {

        public MainNavAdapter(Context context, List<RawMyBoards.Entry> objects) {
            super(context, R.layout.item_board_list, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_board_list, parent, false);

            RawMyBoards.Entry data = getItem(position);
            TextView boardName = ButterKnife.findById(convertView, R.id.boardName);
            DotMark dotMark = ButterKnife.findById(convertView, R.id.dotMark);

            //dotMark.setImageDrawable(new ColorDrawable(data.dotColor));
            dotMark.setImageDrawable(new ColorDrawable(Color.BLUE));
            boardName.setText(data.name);

            convertView.setOnClickListener(v -> onClickItem(position));
            return convertView;
        }
    }
}