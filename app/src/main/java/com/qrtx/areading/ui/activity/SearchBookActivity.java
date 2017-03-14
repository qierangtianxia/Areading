package com.qrtx.areading.ui.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.ui.adapter.SearchBookResultAdapter;
import com.qrtx.areading.utils.BookUtil;
import com.qrtx.areading.utils.DbUtil;
import com.qrtx.areading.utils.GlobalController;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.MD5Util;
import com.qrtx.areading.utils.ProgressDialogUtil;
import com.qrtx.areading.utils.ToastUtils;

import java.util.ArrayList;

public class SearchBookActivity extends AppCompatActivity {
    private ListView mLvLocalBooks;
    private CheckBox mCbCheckAll;
    private Button mBtnAdd;
    private SearchBookResultAdapter mAdapter;
    private ArrayList<String> mBookPathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        initView();
        initEvents();

        mBookPathList = searchBook();
        mAdapter = new SearchBookResultAdapter(this, mBookPathList);
        mLvLocalBooks.setAdapter(mAdapter);
    }

    private void initEvents() {
        //全选按钮
        mCbCheckAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.checkAll(isChecked);
            }
        });
        //加入书架按钮
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> checkList = mAdapter.getCheckList();
                if (checkList == null || checkList.size() <= 0) {
                    ToastUtils.showToast("请至少选择一本书加入书架！");
                    return;
                }
                addBookToNovel();
            }
        });

        mLvLocalBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.checkedPosition(position);
            }
        });
    }

    private void startBookSerch() {
        AsyncTask<Void, Void, ArrayList<String>> searchTask = new AsyncTask<Void, Void, ArrayList<String>>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialogUtil.showProgressDialog(SearchBookActivity.this, "正在搜索．．．");
                mBtnAdd.setEnabled(false);
            }

            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                return searchBook();
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings) {
                super.onPostExecute(strings);
                dialog.dismiss();
                mBtnAdd.setText("加入书架");
                mBtnAdd.setEnabled(true);
            }
        };

        searchTask.execute();
    }

    private ArrayList<String> searchBook() {
        Cursor cursor;
        ContentResolver resolver = getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        cursor = resolver.query(uri, new String[]{MediaStore.MediaColumns.DATA},
                " _data like ?",
                new String[]{"%.txt"}, null, null);
        if (cursor == null || cursor.getColumnCount() <= 0) {
            return null;
        }

        ArrayList<String> bookPathList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int fileIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            String bookPath = cursor.getString(fileIndex);
            if (bookPath.contains(Constants.PATH_BOOK_BASE)) {//如果路径中包含此应用章节存放目录路径，则不加入选择列表
                continue;
            }
            bookPathList.add(bookPath);
        }

        cursor.close();
        return bookPathList;
    }

    private void initView() {
        mLvLocalBooks = (ListView) findViewById(R.id.id_lv_local_books);
        mCbCheckAll = (CheckBox) findViewById(R.id.id_cb_checkall);
        mBtnAdd = (Button) findViewById(R.id.id_btn_addnovel);
    }

    private void addBookToNovel() {
        ArrayList<Integer> checkList = mAdapter.getCheckList();
        if (checkList != null) {

            ArrayList<String> bookIDsFromNovel = BookUtil.getBookIDsFromNovel();
            int count = 0;
            for (Integer integer : checkList) {
                Book book = BookUtil.obtionBook(mBookPathList.get(integer));
                if (bookIDsFromNovel != null && bookIDsFromNovel.contains(book.bookID)) {//如果已经在书架，则不添加
                    continue;
                }

                BookUtil.insertBookToDb(book);
                count++;
            }
            if (count > 0) {
                GlobalController.sNovelIsUpdata = true;
            }
            LogUtil.i("加入书架数据库成功，共加入 " + count + "本书籍。");
        }
        finish();
    }


}
