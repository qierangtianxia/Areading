package com.qrtx.areading.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qrtx.areading.R;
import com.qrtx.areading.utils.PagerWidgetHelper;

/**
 * Created by user on 17-3-15.
 */

public class ChapterlvAdapter extends BaseAdapter {
    private String[] strings;
    private final LayoutInflater inflater;
    private final Resources resources;

    public ChapterlvAdapter(Context context, String[] strings) {
        this.strings = strings;
        inflater = LayoutInflater.from(context);
        resources = context.getResources();
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_textview, parent, false);
        }
        TextView textView = (TextView) convertView;
//        if (PagerWidgetHelper.mCurrentChapter == position) {
//            textView.setTextColor(resources.getColor(R.color.colorPrimary));
//        } else {
//            textView.setTextColor(Color.BLACK);
//        }
        textView.setText("第" + (position + 1) + "章");
        return textView;
    }
}
