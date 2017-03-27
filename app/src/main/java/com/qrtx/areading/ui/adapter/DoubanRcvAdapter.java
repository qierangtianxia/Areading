package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.HotAuthor;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by user on 17-3-15.
 */

public class DoubanRcvAdapter extends ClickableRcvAdapter<DoubanRcvAdapter.MyHolder> {

    private ArrayList<HotAuthor> hotAuthors;
    private final LayoutInflater inflater;

    public DoubanRcvAdapter(Context context, ArrayList<HotAuthor> hotAuthors) {
        this.hotAuthors = hotAuthors;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public DoubanRcvAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder = new MyHolder(inflater.inflate(R.layout.item_book_type_lv, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(DoubanRcvAdapter.MyHolder holder, int position) {
        HotAuthor hotAuthor = hotAuthors.get(position);
        holder.title.setText(hotAuthor.getName());
        holder.summary.setText(hotAuthor.getAlt());
        x.image().bind(holder.avatar, hotAuthor.getAvatarUrl());

        bindListener(holder);
    }

    @Override
    public int getItemCount() {
        return hotAuthors.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView title, summary;

        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.id_item_book_title);
            summary = (TextView) itemView.findViewById(R.id.id_item_book_summary);
            avatar = (ImageView) itemView.findViewById(R.id.id_imv_icon);
        }
    }
}
