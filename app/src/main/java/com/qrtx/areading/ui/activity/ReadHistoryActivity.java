package com.qrtx.areading.ui.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

public class ReadHistoryActivity extends AppCompatActivity {
    private ListView mReadHistoryLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_history);
        mReadHistoryLv = (ListView) findViewById(R.id.id_lv_readhistory);
        mReadHistoryLv.setAdapter(new MyAdapter(getReadHistory()));
    }

    private ArrayList<Book> getReadHistory() {

        Cursor cursor = DbUtil.openDatabase(Constants.DATABASE_NAME).query(Constants.TABLE_NAME_READ_HISTORY, null, null, null, null, null, null);
        if (cursor == null || cursor.getColumnCount() <= 0) {
            return null;
        }
        ArrayList<Book> books = new ArrayList<>();
        while (cursor.moveToNext()) {
            String bookName = cursor.getString(cursor.getColumnIndex("bookName"));
            books.add(new Book(bookName,bookName, null));
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
            TextView textView = new TextView(ReadHistoryActivity.this);
            textView.setText(getItem(position).getBookName());
            return textView;
        }
    }
}
