package com.qrtx.areading.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.ui.fragment.DiscoveryFragment;
import com.qrtx.areading.ui.fragment.NovelFragment;
import com.qrtx.areading.ui.fragment.StoreFragment;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.SPUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PAGER_NOVEL = 0x0000;
    private static final int PAGER_STORE = PAGER_NOVEL + 1;
    private static final int PAGER_DISCOVERY = PAGER_NOVEL + 2;

    private RadioGroup mRadioGroup;
    private ArrayList<Fragment> mFragments;
    private FragmentManager mSfm;
    private NavigationView mNvMain;
    private DrawerLayout mDrawerLayout;

    private TextView mTvUserNick;
    private TextView mTvUserInfor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();

        checkFragment(PAGER_NOVEL);
    }

    private void initData() {
        mSfm = getSupportFragmentManager();

        mFragments = new ArrayList<>();
        mFragments.add(new NovelFragment());
        mFragments.add(new StoreFragment());
        mFragments.add(new DiscoveryFragment());
    }

    private void initEvent() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int i = -1;
                switch (checkedId) {
                    case R.id.id_rb_novel:
                        i = PAGER_NOVEL;
                        break;
                    case R.id.id_rb_store:
                        i = PAGER_STORE;
                        break;
                    case R.id.id_rb_discovery:
                        i = PAGER_DISCOVERY;
                        break;
                }
                if (i >= 0) {
                    checkFragment(i);
                }
            }
        });

        mNvMain.setNavigationItemSelectedListener(this);

        mNvMain.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPUtil.getString(Constants.KEY_USER_TOKEN, null) == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, PersionActivity.class));
                }
            }
        });
    }

    private void initView() {


        mRadioGroup = (RadioGroup) findViewById(R.id.id_radiogroup);
        mNvMain = (NavigationView) findViewById(R.id.id_nav_view);

        View nvMainHeaderView;
        nvMainHeaderView = mNvMain.getHeaderView(0);
        mTvUserNick = (TextView) nvMainHeaderView.findViewById(R.id.id_user_nick);
        mTvUserInfor = (TextView) nvMainHeaderView.findViewById(R.id.id_user_information);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout_main);
        mRadioGroup.check(R.id.id_rb_novel);

        initMainHeaderView();

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new NovelFragment());
        fragments.add(new StoreFragment());
        fragments.add(new DiscoveryFragment());
    }

    private void initMainHeaderView() {

        String nick = SPUtil.getString(Constants.KEY_USER_NICK, null);
        if (nick != null) {
            mTvUserNick.setText(nick);
        } else {
            mTvUserNick.setText("未登录");
        }

        String phone = SPUtil.getString(Constants.KEY_USER_PHONE, null);
        if (phone != null) {
            mTvUserInfor.setText(phone);
        } else {
            mTvUserInfor.setText("请先登陆账号");
        }

        LogUtil.d("LOG OUT nick = " + nick + "   phone = " + phone);
    }

    /**
     * 更改顶部标题
     *
     * @param title
     */
    public void setActionTitle(CharSequence title) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    private void checkFragment(int position) {
        if (position >= mFragments.size()) {
            return;
        }
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            if (i == position) {
                if (!fragment.isAdded()) {
                    mSfm.beginTransaction().add(R.id.id_container, fragment).commit();
                } else {
                    mSfm.beginTransaction().show(fragment).commit();
                }
            } else {
                if (fragment.isAdded()) {
                    mSfm.beginTransaction().hide(fragment).commit();
                }
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_import_book:
                startActivity(new Intent(this, SearchBookActivity.class));
                break;
            case R.id.nav_read_history:
                startActivity(new Intent(this, ReadHistoryActivity.class));
                break;
            case R.id.nav_login_out:
                SPUtil.remove(Constants.KEY_USER_TOKEN);
                SPUtil.remove(Constants.KEY_USER_PHONE);
                SPUtil.remove(Constants.KEY_USER_NICK);
                SPUtil.remove(Constants.KEY_USER_SEX);
                initMainHeaderView();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
