package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qrtx.areading.R;
import com.qrtx.areading.beans.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by user on 17-3-20.
 */

public class CommentLvAdapter extends BaseAdapter {
    private ArrayList<Comment> comments;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;

    public CommentLvAdapter(Context context, ArrayList<Comment> comments) {
        this.comments = comments;
        this.inflater = LayoutInflater.from(context);
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_rcv_commennt, parent, false);
        }

        TextView name, time, content;
        name = (TextView) convertView.findViewById(R.id.id_tv_comment_name);
        time = (TextView) convertView.findViewById(R.id.id_tv_comment_time);
        content = (TextView) convertView.findViewById(R.id.id_tv_comment_content);


        Comment comment = comments.get(position);
        name.setText(comment.getUserName());
        time.setText(dateFormat.format(comment.getTime()));
//        new Data();
//        time.setText(comment.getTime());
        content.setText(comment.getContent());
        return convertView;
    }
}
