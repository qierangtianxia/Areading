package com.qrtx.areading.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.ui.activity.ReadActivity;
import com.qrtx.areading.ui.activity.ReadHistoryActivity;
import com.qrtx.areading.ui.adapter.NovelRcvAdapter;
import com.qrtx.areading.utils.BookUtil;
import com.qrtx.areading.utils.DbUtil;
import com.qrtx.areading.utils.GlobalController;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.OnKeyShareUtil;

import java.util.ArrayList;

/**
 * Created by user on 17-2-28.
 */

public class NovelFragment extends BaseFragment {
    private RecyclerView mNovelRcv;
    private FloatingActionButton mReadHistoryFab;
    private FloatingActionButton mNovelSettingFab;
    private ArrayList<Book> mBookList;
    private GridLayoutManager mGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private NovelRcvAdapter mNovelRcvAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novel, container, false);
        mBootActivity = getActivity();
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionTitle(Constants.TITLE_PAGER_NOVEL);
        initData();
    }

    private void initData() {


        mBookList = BookUtil.getBookFromNovelDb();

        if (mBookList == null) {
            String content = Constants.BODY;
            mBookList = new ArrayList<>();

            for (int j = 0; j < 3; j++) {
                Book book = new Book("0000" + j, "0000" + j, null);
                //模拟数据 100 章数据
                for (int i = 0; i < 10; i++) {
                    Book.Chapter chapter = new Book.Chapter(i + "", "第 " + (i + 1) + " 章", content);
                    book.chapterList.add(chapter);
                }
                mBookList.add(book);
            }
        }
        mGridLayoutManager = new GridLayoutManager(mBootActivity, 3);
        mLinearLayoutManager = new LinearLayoutManager(mBootActivity);
        mNovelRcv.setLayoutManager(mGridLayoutManager);
        mNovelRcvAdapter = new NovelRcvAdapter(mBootActivity, mBookList);
        mNovelRcv.setAdapter(mNovelRcvAdapter);
        LogUtil.i("mNovelRcvAdapter = " + mNovelRcvAdapter);


        mNovelRcvAdapter.setOnItemClickLitener(new NovelRcvAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mBootActivity, ReadActivity.class);

                intent.putExtra(Constants.KEY_BOOK, mBookList.get(position));
                mBootActivity.startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                showDialog(position);
                return true;
            }
        });

    }


    private void initView(View contentView) {
        mNovelRcv = (RecyclerView) contentView.findViewById(R.id.id_novel_rylview);
        mReadHistoryFab = (FloatingActionButton) contentView.findViewById(R.id.id_read_history_fab);
        mNovelSettingFab = (FloatingActionButton) contentView.findViewById(R.id.id_setting_fab);


        mReadHistoryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mBootActivity, "跳转阅读记录", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mBootActivity, ReadHistoryActivity.class));
            }
        });
        mNovelSettingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.LayoutManager layoutManager = mNovelRcv.getLayoutManager();
                if (layoutManager == mLinearLayoutManager) {
                    mNovelRcv.setLayoutManager(mGridLayoutManager);
                } else {
                    mNovelRcv.setLayoutManager(mLinearLayoutManager);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setActionTitle(Constants.TITLE_PAGER_NOVEL);
        if (GlobalController.sNovelIsUpdata) {
            ArrayList<Book> newBookList = BookUtil.getBookFromNovelDb();
            mBookList.clear();
            mBookList.addAll(newBookList);
            mNovelRcvAdapter.notifyDataSetChanged();
            GlobalController.sNovelIsUpdata = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setActionTitle(Constants.TITLE_PAGER_NOVEL);
        }
    }

    private void removeBookFromNovel(int position) {
        if (position >= mBookList.size()) {
            return;
        }
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        int delete = database.delete(Constants.TABLE_NAME_BOOK_NOVEL, "bookId=?", new String[]{mBookList.get(position).bookID});
        if (delete > 0) {
            LogUtil.i("remove position = " + position);
            BookUtil.removeBookFromLocal(mBookList.get(position));
            mBookList.remove(position);
            mNovelRcvAdapter.notifyItemRemoved(position);
        }
    }

    private void showDialog(final int position) {
        final String[] arrayFruit = new String[]{"分享这本书", "从书架移除"};

        Dialog alertDialog = new AlertDialog.Builder(mBootActivity)
                .setItems(arrayFruit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0://分享
                                shareBook(position);
                                break;
                            case 1://移除
                                removeBookFromNovel(position);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alertDialog.show();
    }

    private void shareBook(int position) {
        Book book = mBookList.get(position);
        OnKeyShareUtil.showShare("看书喽", "小伙伴，这本《" + book.getBookName() + "》真好看，一起来看吧！！！");
    }
}
