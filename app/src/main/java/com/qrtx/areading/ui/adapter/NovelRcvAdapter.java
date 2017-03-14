package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by user on 17-3-2.
 */

public class NovelRcvAdapter extends RecyclerView.Adapter<NovelRcvAdapter.MyViewHolder> {
    private ArrayList<Book> books;
    private LayoutInflater inflater;

    public NovelRcvAdapter(Context ctx, ArrayList<Book> books) {
        this.books = books;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_novel_rcv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    private Book getItemBean(int position) {
        return books.get(position);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Book book = getItemBean(position);
        holder.bookName.setText(book.getBookName());
        if (book.getBookIcon() != null) {
            holder.bookIcon.setImageBitmap(book.getBookIcon());
        }
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(v, pos);

                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    return mOnItemClickLitener.onItemLongClick(v, pos);
                }
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookIcon;
        private TextView bookName;

        public MyViewHolder(View itemView) {
            super(itemView);
            bookIcon = (ImageView) itemView.findViewById(R.id.id_book_icon);
            bookName = (TextView) itemView.findViewById(R.id.id_book_name);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}

