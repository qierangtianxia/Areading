package com.qrtx.areading.ui.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.utils.BookUtil;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.PagerWidgetHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadActivity extends Activity {


    private PagerWidgetHelper mPagerWidgetHelper;
    private FrameLayout mFrameLayout;
    private Book mCurrentBook;
    private View mSettingViewCeil, mSettingViewBottom;
    private boolean isSettingViewShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);


        mFrameLayout = (FrameLayout) findViewById(R.id.flReadWidget);
        mCurrentBook = (Book) getIntent().getSerializableExtra(Constants.KEY_BOOK);

        mSettingViewCeil = findViewById(R.id.id_read_setting_ceil);
        mSettingViewBottom = findViewById(R.id.id_read_setting_bottom);

//        final Book book = new Book("915915", "哈哈", null);
//        book.setBookPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mybooktest.txt");
//
//
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    BookUtil.clipBookChapterListFroLocal(book);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();


//        new FoundBookTask().execute(book.getBookPath());
        mPagerWidgetHelper = PagerWidgetHelper.init(ReadActivity.this, mFrameLayout, mCurrentBook);

        mPagerWidgetHelper.setOnCenterClickListener(new PagerWidgetHelper.OnCenterClickListener() {
            @Override
            public void onCenterClick() {
                LogUtil.i("onCenterClick");
                mSettingViewCeil.setVisibility(isSettingViewShow ? View.GONE : View.VISIBLE);
                mSettingViewBottom.setVisibility(isSettingViewShow ? View.GONE : View.VISIBLE);
//                mSettingViewCeil.setVisibility(View.VISIBLE);
//                mSettingViewBottom.setVisibility(View.VISIBLE);
                isSettingViewShow = !isSettingViewShow;
            }
        });
    }

    private class FoundBookTask extends AsyncTask<String, Void, Book> {
        @Override
        protected Book doInBackground(String... params) {

            String content = null;
            try {
                File file = new File(params[0]);
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                char[] chars = new char[1024];
                int read = reader.read(chars);
                content = new String(chars, 0, read);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Book book = new Book("00001", null, null);

            //模拟数据 100 章数据
            for (int i = 0; i < 100; i++) {
                Book.Chapter chapter = new Book.Chapter(i + "", "第 " + (i + 1) + " 章", content);
                book.chapterList.add(chapter);
            }
            return book;
        }

        @Override
        protected void onPostExecute(Book book) {
            super.onPostExecute(book);
            mPagerWidgetHelper = PagerWidgetHelper.init(ReadActivity.this, mFrameLayout, book);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPagerWidgetHelper.recycle();
    }
}
