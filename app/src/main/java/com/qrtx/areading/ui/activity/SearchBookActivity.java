package com.qrtx.areading.ui.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.ui.adapter.SearchBookResultAdapter;
import com.qrtx.areading.utils.BookUtil;
import com.qrtx.areading.utils.GlobalController;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.ProgressDialogUtil;
import com.qrtx.areading.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;

public class SearchBookActivity extends AppCompatActivity {
    private ListView mLvLocalBooks;
    private ToggleButton mCbCheckAll;
    private Button mBtnAdd;
    private SearchBookResultAdapter mAdapter;
    private ArrayList<String> mBookPathList;


    private FileSannerClient mFileSannerClient;
    private MediaScannerConnection mMediaScannerConnection;
    private File mFilePath = null;
    private String mFileType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new Thread(new Runnable() {
            @Override
            public void run() {
                mFileSannerClient = new FileSannerClient();
                mMediaScannerConnection = new MediaScannerConnection(SearchBookActivity.this, mFileSannerClient);
                scanfile(new File(Environment.getExternalStorageDirectory() + ""));
            }
        }).start();

        setContentView(R.layout.activity_search_book);
        setTitle(R.string.searchBook);
        initView();
        initEvents();

        new SearchBookTask().execute();
    }

    private void initEvents() {
        //全选按钮
        mCbCheckAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mBookPathList.size() <= 0) {
                    ToastUtils.showToast("没有找到还未加入书架的书！");
                    mCbCheckAll.setChecked(false);
                    return;
                }
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
                if (mAdapter.isChecked(position)) {
                    mAdapter.disCheckedPosition(position);
                } else {
                    mAdapter.checkedPosition(position);
                }
            }
        });
    }


    private ArrayList<String> searchBook() {
        Cursor cursor;
        ContentResolver resolver = getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        cursor = resolver.query(uri, new String[]{MediaStore.MediaColumns.DATA},
                " _data like ?", new String[]{"%.txt"}, null, null);
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
            if (BookUtil.hasAddToNovel(bookPath)) {//如果当前路径下，已经添加至书架，则不添加
                continue;
            }
            bookPathList.add(bookPath);
        }

        cursor.close();
        return bookPathList;
    }

    private void initView() {
        mLvLocalBooks = (ListView) findViewById(R.id.id_lv_local_books);
        mCbCheckAll = (ToggleButton) findViewById(R.id.id_tbtn_checkall);
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

    //扫描文件
    private class FileSannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

        @Override
        public void onMediaScannerConnected() {
            LogUtil.i("<-------onMediaScannerConnected------>");
            if (mFilePath != null) {
                if (mFilePath.isDirectory()) {
                    File[] files = mFilePath.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            if (files[i].isDirectory())
                                scanfile(files[i]);
                            else {
                                mMediaScannerConnection.scanFile(
                                        files[i].getAbsolutePath(), mFileType);
                            }
                        }
                    }
                }
            }

            mFilePath = null;

            mFileType = null;

        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mMediaScannerConnection.disconnect();
            LogUtil.i("<-------onScanCompleted------>");
        }
    }

    private void scanfile(File f) {
        this.mFilePath = f;
        mMediaScannerConnection.connect();
    }

    //因为数据库查询是一项耗时操作　　所以必须在子线程下操作
    class SearchBookTask extends AsyncTask<Void, Void, ArrayList<String>> {


        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialogUtil.showProgressDialog(SearchBookActivity.this, "正在搜索．．．");
        }


        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            return searchBook();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            mBookPathList = searchBook();
            mAdapter = new SearchBookResultAdapter(SearchBookActivity.this, mBookPathList);
            mLvLocalBooks.setAdapter(mAdapter);
            progressDialog.dismiss();
        }
    }
}
