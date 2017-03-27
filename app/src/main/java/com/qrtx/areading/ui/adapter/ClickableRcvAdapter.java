package com.qrtx.areading.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 17-3-16.
 */

public abstract class ClickableRcvAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    protected void bindListener(final H holder) {
        if (mOnItemClickLitener == null) {
            return;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(v, pos);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemLongClick(v, pos);
                return true;
            }
        });
    }
}
