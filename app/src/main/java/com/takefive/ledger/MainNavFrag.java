package com.takefive.ledger;

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

import com.takefive.ledger.client.raw.DidGetBoard;
import com.takefive.ledger.client.LedgerService;
import com.takefive.ledger.database.RealmAccess;
import com.takefive.ledger.database.UserStore;
import com.takefive.ledger.model.Person;
import com.takefive.ledger.ui.DotMark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
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
    ArrayList<DidGetBoard.Entry> mListData = new ArrayList<>();
    MainNavAdapter mListAdapter;

    @Inject
    RealmAccess realmAccess;

    @Inject
    UserStore userStore;

    @Inject
    LedgerService service;

    @Inject
    ActionChainFactory chainFactory;

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

        // Retrieve current user
        // TODO: provide a way to refresh
        chainFactory.get(fail -> fail.getCause().printStackTrace()
        ).netThen(() -> {
            return realmAccess.process(realm -> {
                return realm.where(Person.class)
                        .equalTo("personId", userStore.getMostRecentUserId())
                        .findFirst().getName();
            });
        }).uiConsume((String name) -> {
            mUserName.setText(name);
        }).start();

        // init list
        mListAdapter = new MainNavAdapter(getContext(), mListData);
        mList.setAdapter(mListAdapter);
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
        });

        // add data
        chainFactory.get(errorHolder -> {
            showInfo("Cannot get boards: " + errorHolder.getCause().toString());
            errorHolder.getCause().printStackTrace();
        }).netConsume(obj -> {
            Response<DidGetBoard> resp = service.getMyBoards().execute();
            if (!resp.isSuccessful()) {
                String msg = resp.errorBody().string();
                resp.errorBody().close();
                throw new IOException(msg);
            }
            mListData.addAll(resp.body().boards);
        }).uiConsume(obj -> {
            mListAdapter.notifyDataSetChanged();
        }).start();

        return root;
    }


    public void showInfo(String info) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }

    public void showInfo(int info) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), info, Snackbar.LENGTH_SHORT).show();
    }
}

class MainNavAdapter extends ArrayAdapter<DidGetBoard.Entry> {

    public MainNavAdapter(Context context, List<DidGetBoard.Entry> objects) {
        super(context, R.layout.item_board_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_board_list, parent, false);

        DidGetBoard.Entry data = getItem(position);
        TextView boardName = ButterKnife.findById(convertView, R.id.boardName);
        DotMark dotMark = ButterKnife.findById(convertView, R.id.dotMark);

        //dotMark.setImageDrawable(new ColorDrawable(data.dotColor));
        dotMark.setImageDrawable(new ColorDrawable(Color.BLUE));
        boardName.setText(data.name);
        return convertView;
    }
}
