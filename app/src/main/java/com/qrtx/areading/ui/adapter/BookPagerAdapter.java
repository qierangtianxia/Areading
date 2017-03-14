package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.qrtx.areading.ui.view.BookPagerView;
import com.qrtx.areading.utils.LogUtil;

import java.io.FileNotFoundException;

/**
 * Created by user on 17-3-2.
 */

public class BookPagerAdapter extends PagerAdapter {
    private Context ctx;
    private String content;

    public BookPagerAdapter(Context ctx, String content) {
        this.ctx = ctx;
        this.content = content;
    }

    int pagerCount = Integer.MAX_VALUE;

    @Override
    public int getCount() {
        LogUtil.e("<-------------getCount--------->");
        return pagerCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BookPagerView bookPagerView = null;
        try {
            bookPagerView = new BookPagerView(ctx);
            bookPagerView.setContent(content);
            container.addView(bookPagerView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bookPagerView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
