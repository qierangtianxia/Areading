package com.qrtx.areading.ui.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.utils.DbUtil;
import com.qrtx.areading.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ReadHistoryActivity extends AppCompatActivity {
    private ListView mReadHistoryLv;
    private ArrayList<String> mTimeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_history);
        setTitle(R.string.readHistory);

        mTimeList = new ArrayList<>();
        mReadHistoryLv = (ListView) findViewById(R.id.id_lv_readhistory);
        mReadHistoryLv.setAdapter(new LvAdapter(getReadHistory()));
    }

    private ArrayList<Book> getReadHistory() {

        Cursor cursor = DbUtil.openDatabase(Constants.DATABASE_NAME).query(Constants.TABLE_NAME_READ_HISTORY, null, null, null, null, null, null);
        if (cursor == null || cursor.getColumnCount() <= 0) {
            return null;
        }
        ArrayList<Book> books = new ArrayList<>();
        while (cursor.moveToNext()) {
            String bookName = cursor.getString(cursor.getColumnIndex("bookName"));
            String readTime = cursor.getString(cursor.getColumnIndex("readTime"));
            String bookPath = cursor.getString(cursor.getColumnIndex("bookPath"));
            String bookId = cursor.getString(cursor.getColumnIndex("_id"));

//            LogUtil.i("readTime.class = " + readTime.getClass().getSimpleName());
            mTimeList.add(readTime);
            Book book = new Book(bookId, bookName, null);
            book.setBookPath(bookPath);
            books.add(book);
        }
        return books;
    }

    class MyAdapter extends BaseAdapter {
        private ArrayList<Book> books;

        public MyAdapter(ArrayList<Book> books) {
            this.books = books;
        }

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Book getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_book_type_lv, parent, false);
            }

            return convertView;
        }
    }

    private class LvAdapter extends BaseAdapter {
        private ArrayList<Book> books;

        public LvAdapter(ArrayList<Book> books) {
            this.books = books;
        }

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Book getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        String timeHead = "";

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
//        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_test, parent, false);
            }

            TextView bookInfo = (TextView) convertView.findViewById(R.id.tv_diary);
            TextView time = (TextView) convertView
                    .findViewById(R.id.tv_date_title);
            long timeLong = Long.valueOf(mTimeList.get(position));


            bookInfo.setText("《" + getItem(position).getBookName() + "》");
            time.setText(dateFormat1.format(timeLong));

//            if (timeHead.equals("")) {
//                timeHead = dateFormat2.format();
//            }

//            if (position == 3 || position == 1 || position == 5) {
//                tv2.setVisibility(View.GONE);
//            } else {
//                tv2.setVisibility(View.VISIBLE);
//            }
//            tv2.setText(mDate[position]);
//            tv.setText(getItem(position));
            return convertView;
        }

    }
}
