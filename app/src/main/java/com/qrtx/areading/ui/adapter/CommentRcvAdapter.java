package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qrtx.areading.R;
import com.qrtx.areading.beans.Comment;

import java.util.ArrayList;

/**
 * Created by user on 17-3-20.
 */

public class CommentRcvAdapter extends ClickableRcvAdapter<CommentRcvAdapter.MyHolder> {
    private ArrayList<Comment> comments;
    private LayoutInflater inflater;

    public CommentRcvAdapter(Context context, ArrayList<Comment> comments) {
        this.comments = comments;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CommentRcvAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder myHolder = new MyHolder(inflater.inflate(R.layout.item_rcv_commennt, parent, false));
        return myHolder;
    }

    @Override
    public void onBindViewHolder(CommentRcvAdapter.MyHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.name.setText(comment.getUserName());
//        holder.time.setText(comment.getTime());
        holder.content.setText(comment.getContent());
        bindListener(holder);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView name, time, content;

        public MyHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.id_tv_comment_name);
            time = (TextView) itemView.findViewById(R.id.id_tv_comment_time);
            content = (TextView) itemView.findViewById(R.id.id_tv_comment_content);
        }
    }
}
