package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.BookType;
import com.qrtx.areading.ui.activity.BookDetailActivity;
import com.qrtx.areading.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by user on 17-3-14.
 */

public class BookTypeAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<BookType> bookTypes;
    private HashMap<Integer, ArrayList<Book>> bookMap;


    public BookTypeAdapter(Context context, ArrayList<BookType> bookTypes, HashMap<Integer, ArrayList<Book>> bookMap) {
        this.context = context;
        this.bookMap = bookMap;
        this.bookTypes = bookTypes;
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
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final ArrayList<Book> books = bookMap.get(bookTypes.get(position).id);
        RcvAdapter rcvAdapter = new RcvAdapter(books);

        rcvAdapter.setOnItemClickLitener(new ClickableRcvAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra(Constants.KEY_BOOK, books.get(position));
                context.startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                return false;
            }
        });
        recyclerView.setAdapter(rcvAdapter);
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


    private class RcvAdapter extends ClickableRcvAdapter<RcvAdapter.MyHolder> {
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
            bindListener(holder);
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
