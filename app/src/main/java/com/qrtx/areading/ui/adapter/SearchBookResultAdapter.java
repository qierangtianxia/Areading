package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.utils.BookUtil;

import java.util.ArrayList;

/**
 * Created by user on 17-3-8.
 */

public class SearchBookResultAdapter extends BaseAdapter {
    private Context ctx;
    private ArrayList<String> bookPathList;
    private ArrayList<Integer> checkPositionList;


    public SearchBookResultAdapter(Context ctx, ArrayList<String> bookPathList) {
        this.ctx = ctx;
        this.bookPathList = bookPathList;
    }

    @Override
    public int getCount() {
        return bookPathList.size();
    }

    @Override
    public String getItem(int position) {
        return bookPathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.item_search_book_result, null);
            MyViewHolder viewHolder = new MyViewHolder();
            viewHolder.textViewPath = (TextView) convertView.findViewById(R.id.id_book_path);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.id_book_name);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.id_cb_book_ischeck);
            convertView.setTag(viewHolder);
        }
        MyViewHolder viewHolder = (MyViewHolder) convertView.getTag();

        Book book = BookUtil.obtionBook(getItem(position));

        viewHolder.textViewPath.setText(getItem(position));
        viewHolder.textViewName.setText("书名：" + book.getBookName());
        viewHolder.checkBox.setChecked(isChecked(position));
        return convertView;
    }

    private class MyViewHolder {
        TextView textViewPath, textViewName;
        CheckBox checkBox;
    }

    public void checkedPosition(int position) {
        if (checkPositionList == null) {
            checkPositionList = new ArrayList<>();
        }
        if (!checkPositionList.contains(position)) {
            checkPositionList.add(position);
            notifyDataSetChanged();
        }
    }

    public void disCheckedPosition(int position) {
        if (checkPositionList == null) {
            checkPositionList = new ArrayList<>();
        }
        if (checkPositionList.contains(position)) {
            checkPositionList.remove(Integer.valueOf(position));
            notifyDataSetChanged();
        }
    }

    public boolean isChecked(int position) {
        if (checkPositionList == null) {
            return false;
        }
        return checkPositionList.contains(position);
    }

    public void checkAll(boolean checkAll) {
        if (checkPositionList == null) {
            checkPositionList = new ArrayList<>();
        }
        if (checkAll) {
            for (int i = 0; i < bookPathList.size(); i++) {
                checkedPosition(i);
            }
        } else {
            checkPositionList.clear();
        }

        notifyDataSetChanged();
    }

    public ArrayList<Integer> getCheckList() {
        return checkPositionList;
    }
}
