package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.BookType;
import com.qrtx.areading.ui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 17-3-14.
 */

public class BookTypeAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<BookType> bookTypes;
    private HashMap<Integer, ArrayList<Book>> bookMap;
//    private ArrayList<Fragment> fragments;


    public BookTypeAdapter(Context context, ArrayList<BookType> bookTypes) {
        this.context = context;
        this.bookTypes = bookTypes;
        bookMap = new HashMap<>();
        initData();
    }

    private void initData() {
        for (int i = 0; i < bookTypes.size(); i++) {
            ArrayList<Book> books = new ArrayList<>();
            String typeName = bookTypes.get(i).name;
            for (int j = 0; j < 15; j++) {
                Book book = new Book(null, typeName + (j + 1), null);
                book.setSummary("我是一本" + typeName + "类型的书");
                books.add(book);
            }
            bookMap.put(i, books);
        }
    }

    @Override
    public int getCount() {
        return bookTypes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        ListView listView = new ListView(context);
//        listView.setAdapter(new BookTypeLvAdapter(bookMap.get(position)));
//        container.addView(listView);
//        return listView;
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new RcvAdapter(bookMap.get(position)));
        container.addView(recyclerView);
        return recyclerView;
    }

    private BookType getItem(int position) {
        return bookTypes.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getItem(position).name;
    }

    private class BookTypeLvAdapter extends BaseAdapter {
        private ArrayList<Book> books;

        public BookTypeLvAdapter(ArrayList<Book> books) {
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
                convertView = View.inflate(context, R.layout.item_book_type_lv, null);
            }

            TextView title = (TextView) convertView.findViewById(R.id.id_item_book_title);
            TextView summary = (TextView) convertView.findViewById(R.id.id_item_book_summary);
            Book book = getItem(position);
            title.setText(book.getBookName());
            summary.setText(book.getSummary());
            return convertView;
        }
    }

    private class RcvAdapter extends RecyclerView.Adapter<RcvAdapter.MyHolder> {
        private ArrayList<Book> books;

        public RcvAdapter(ArrayList<Book> books) {
            this.books = books;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_book_type_lv, parent, false);
            MyHolder myHolder = new MyHolder(view);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            Book book = books.get(position);
            holder.title.setText(book.getBookName());
            holder.summary.setText(book.getSummary());
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView title, summary;

            public MyHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.id_item_book_title);
                summary = (TextView) itemView.findViewById(R.id.id_item_book_summary);
            }
        }
    }
}
