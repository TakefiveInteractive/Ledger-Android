package com.takefive.ledger.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;
import com.takefive.ledger.Helpers;
import com.takefive.ledger.MyApplication;
import com.takefive.ledger.R;
import com.takefive.ledger.dagger.fb.BusinessFbLoginResult;
import com.takefive.ledger.midData.ledger.RawMyBoards;
import com.takefive.ledger.midData.ledger.RawPerson;
import com.takefive.ledger.dagger.UserStore;
import com.takefive.ledger.model.Person;
import com.takefive.ledger.presenter.utils.RealmAccess;
import com.takefive.ledger.view.utils.DotMark;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zyu19.libs.action.chain.ActionChainFactory;

/**
 * Created by zyu on 2/13/16.
 */
public class MainNavFrag extends Fragment {

    public static final int NEW_BOARD_REQUEST = 0;
    @Bind(R.id.profile_name_text)
    TextView mUserName;
    @Bind(R.id.chosen_account_view)
    FrameLayout mSideImgLayout;
    @Bind(R.id.chosen_account_content_view)
    RelativeLayout mSideImgContent;
    @Bind(R.id.boardList)
    ListView mList;
    @Bind(R.id.newBoard)
    BootstrapButton mNewBoard;
    @Bind(R.id.profile_image)
    BootstrapCircleThumbnail mAvatar;


    ArrayList<RawMyBoards.Entry> mListData = new ArrayList<>();
    MainNavAdapter mListAdapter;

    @Inject
    RealmAccess realmAccess;

    @Inject
    UserStore userStore;

    @Inject
    ActionChainFactory chainFactory;

    boolean isInitialized;

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

        isInitialized = false;

        // TODO: provide a graphical interface to refresh (That logic is ready, though)

        chainFactory.get(fail -> {
            fail.getCause().printStackTrace();
            ((MainActivity) getActivity()).showAlert(fail.getCause().getMessage());
        }).netThen(() -> realmAccess.process(realm -> {
            Person me = realm.where(Person.class)
                    .equalTo("personId", userStore.getMostRecentUserId())
                    .findFirst();
            if(!me.load())
                throw new Exception(getString(R.string.ex_cannot_load_realm));
            return me.getAvatarUrl();
        })).uiConsume((String url) -> {
            Picasso.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.person_image_empty)
                    .error(R.drawable.person_image_empty)
                    .fit()
                    .into(mAvatar);
        }).start();

        mNewBoard.setOnClickListener(v -> {
            new NewBoardFragment().show(getActivity().getSupportFragmentManager(), "fragment_new_board");
            ((MainActivity) getActivity()).closeDrawers();
        });
        // init list
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            onClickItem(position);
        });

        MainActivity mainActivity = ((MainActivity) getActivity());
        mainActivity.presenter.loadMyUserInfo();
        mainActivity.presenter.loadMyBoards();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_BOARD_REQUEST:
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void showMyUserInfo(RawPerson me) {
        mUserName.setText(me.name);
    }

    void showMyBoards(RawMyBoards myBoards) {
        MainNavAdapter adapter = new MainNavAdapter(getContext(), myBoards.boards);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mListAdapter = adapter;
        if (!isInitialized) {
            onClickItem(0);
            isInitialized = true;
        }
    }

    void onClickItem(int pos) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.presenter.loadBills(mListAdapter.getItem(pos).id);
        mainActivity.presenter.refreshBoardInfo(mListAdapter.getItem(pos));
        mainActivity.closeDrawers();
    }

    @OnClick(R.id.logout)
    void logout() {
        ((MainActivity) getActivity()).presenter.logout(new BusinessFbLoginResult(AccessToken.getCurrentAccessToken()));
    }

    class MainNavAdapter extends ArrayAdapter<RawMyBoards.Entry> {

        public MainNavAdapter(Context context, List<RawMyBoards.Entry> objects) {
            super(context, R.layout.item_board_list, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_board_list, parent, false);

            convertView.setOnClickListener(v -> ((MainActivity) getActivity()).closeDrawers());

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
