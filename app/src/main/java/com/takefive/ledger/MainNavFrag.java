package com.takefive.ledger;

import android.content.Context;
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

import com.takefive.ledger.database.UserStore;
import com.takefive.ledger.model.Person;
import com.takefive.ledger.ui.DotMark;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

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
    ArrayList<MainNavData> mListData = new ArrayList<>();
    MainNavAdapter mListAdapter;

    @Inject
    Realm realm;

    @Inject
    UserStore userStore;

    private Person currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.drawer_main, container, false);
        ButterKnife.bind(this, root);
        ((MyApplication) getActivity().getApplication()).inject(this);

        // Fix drawer location after enabling status bar transparency.
        // Lollipop <=> v21, corresponding with values-v21
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.LayoutParams p = mSideImgLayout.getLayoutParams();
            p.height += Helpers.getStatusBarHeight(getResources());
            mSideImgLayout.setLayoutParams(p);

            Helpers.setMargins(mSideImgContent, Helpers.getStatusBarHeight(getResources()), null, null, null);
        }

        // Retrieve current user
        currentUser = realm.where(Person.class)
                .equalTo("personId", userStore.getMostRecentUserId())
                .findFirst();
        if(currentUser != null)
            mUserName.setText(currentUser.getName());

        // init list
        mListAdapter = new MainNavAdapter(getContext(), mListData);
        mList.setAdapter(mListAdapter);
        mList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {});

        // add data
        for(int i = 0; i < 20; i++)
            mListData.add(new MainNavData(Color.GREEN, "[Generated name] Board " + i));
        mListAdapter.notifyDataSetChanged();

        return root;
    }
}

class MainNavData {
    public MainNavData(int c, String n) {
        dotColor = c;
        name = n;
    }
    public int dotColor;
    public String name;
}

class MainNavAdapter extends ArrayAdapter<MainNavData> {

    public MainNavAdapter(Context context, List<MainNavData> objects) {
        super(context, R.layout.item_board_list, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_board_list, parent, false);

        MainNavData data = getItem(position);
        TextView boardName = ButterKnife.findById(convertView, R.id.boardName);
        DotMark dotMark = ButterKnife.findById(convertView, R.id.dotMark);

        dotMark.setImageDrawable(new ColorDrawable(data.dotColor));
        boardName.setText(data.name);
        return convertView;
    }
}