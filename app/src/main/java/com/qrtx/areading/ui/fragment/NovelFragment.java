package com.qrtx.areading.ui.fragment;

import android.content.ContentValues;
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

                SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
                ContentValues contentValues = new ContentValues();
                contentValues.put("_id", 1);
                contentValues.put("bookName", mBookList.get(position).getBookName());
                database.insert(Constants.TABLE_NAME_READ_HISTORY, null, contentValues);


            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                showNormalDialog(position);
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
        LogUtil.i("GlobalController.sNovelIsUpdata = " + GlobalController.sNovelIsUpdata);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setActionTitle(Constants.TITLE_PAGER_NOVEL);
        }
    }

    private void showNormalDialog(final int position) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mBootActivity);
        normalDialog.setMessage("是否想要讲＝将这本书从书架移除?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeBookFromNovel(position);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void removeBookFromNovel(int position) {
        if (position >= mBookList.size()) {
            return;
        }
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        int delete = database.delete(Constants.TABLE_NAME_BOOK_NOVEL, "bookId=?", new String[]{mBookList.get(position).bookID});
        if (delete > 0) {
            LogUtil.i("remove position = " + position);
            mBookList.remove(position);
            mNovelRcvAdapter.notifyItemRemoved(position);
        }
    }
}
