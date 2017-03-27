package com.qrtx.areading.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.HotAuthor;
import com.qrtx.areading.net.SimpleCommonCallback;
import com.qrtx.areading.net.api.DoubanAPI;
import com.qrtx.areading.ui.activity.HotAuthorActivity;
import com.qrtx.areading.ui.adapter.ClickableRcvAdapter;
import com.qrtx.areading.ui.adapter.DoubanRcvAdapter;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 17-2-28.
 */

public class DiscoveryFragment extends BaseFragment {
    private ProgressBar mProgressBar;
    private RecyclerView mDoubanRcv;
    private int mNetStartPos = 20;
    private int mNetCount = 20;
    private SwipeRefreshLayout mRefreshLayout;
    private ArrayList<HotAuthor> mHotAuthors;
    private DoubanRcvAdapter mDoubanRcvAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionTitle(Constants.TITLE_PAGER_DISCOVERY);
        View bootView = addContentView(R.layout.fragment_discovery);
        initView(bootView);
    }

    private void initView(View bootView) {
        mProgressBar = (ProgressBar) bootView.findViewById(R.id.id_pb_discovery);
        initRefreshLayout(bootView);
        initListView(bootView);
    }

    private void initRefreshLayout(View bootView) {
        mRefreshLayout = (SwipeRefreshLayout) bootView.findViewById(R.id.id_refresh);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHotHotAuthorList(mNetStartPos, mNetCount);
            }
        });
    }

    private void initListView(View bootView) {
        mDoubanRcv = (RecyclerView) bootView.findViewById(R.id.id_rcv_douban);
        getHotHotAuthorList(mNetStartPos, mNetCount);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setActionTitle(Constants.TITLE_PAGER_DISCOVERY);
        }
    }

    public ArrayList<HotAuthor> getHotHotAuthorList(int start, int count) {
        LogUtil.i("start = " + start + "      count = " + count);
        RequestParams entity = new RequestParams();
        entity.addBodyParameter(Constants.KEY_COUNT, start + "");
        entity.addBodyParameter(Constants.KEY_START, count + "");
        entity.setUri(DoubanAPI.GetHotAuthor);
        x.http().get(entity, new SimpleCommonCallback() {

            @Override
            public void onSuccess(String result) {
                LogUtil.i("result = " + result);
                mRefreshLayout.setRefreshing(false);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray authorsJson = resultJson.getJSONArray("authors");
                    mHotAuthors = new ArrayList<>();
                    for (int i = 0; i < authorsJson.length(); i++) {
                        JSONObject authorJson = authorsJson.optJSONObject(i);
                        String id = authorJson.getString("id");
                        String name = authorJson.getString("name");
                        String resume = authorJson.getString("resume");
                        String persionPagerUrl = authorJson.getString("url");
                        String avatarUrl = authorJson.getString("avatar");
                        String editorNotes = authorJson.getString("editor_notes");
                        String avatarUrlLarge = authorJson.getString("large_avatar");
                        String alt = authorJson.getString("alt");
                        String lastPostTime = authorJson.getString("last_post_time");
                        HotAuthor hotAuthor = new HotAuthor(id, name, resume, persionPagerUrl, avatarUrl, avatarUrlLarge, lastPostTime, editorNotes, alt);
                        mHotAuthors.add(hotAuthor);
                    }

                    LogUtil.i(" mHotAuthors.size() = " + mHotAuthors.size());
                    Collections.reverse(mHotAuthors);

                    mDoubanRcvAdapter = new DoubanRcvAdapter(mBootActivity, mHotAuthors);
                    mDoubanRcvAdapter.setOnItemClickLitener(new ClickableRcvAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(mBootActivity, HotAuthorActivity.class);
                            intent.putExtra(Constants.KEY_HOTAUTHOR, mHotAuthors.get(position));
                            startActivity(intent);
                        }

                        @Override
                        public boolean onItemLongClick(View view, int position) {
                            return false;
                        }
                    });
                    mDoubanRcv.setLayoutManager(new LinearLayoutManager(mBootActivity));
                    mDoubanRcv.setAdapter(mDoubanRcvAdapter);
                    mDoubanRcvAdapter.notifyDataSetChanged();
                    mNetStartPos += mNetCount;
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mProgressBar.setVisibility(View.GONE);
                    mDoubanRcv.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("ex = " + ex);
                mProgressBar.setVisibility(View.GONE);
                ToastUtils.showToast("加载失败");
            }
        });

        return null;
    }
}
