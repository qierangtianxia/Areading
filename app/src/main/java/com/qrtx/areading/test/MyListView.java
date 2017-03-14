package com.qrtx.areading.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.qrtx.areading.utils.LogUtil;

/**
 * Created by user on 17-3-10.
 */

public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtil.i("TOUCH TEST", "MyListView--->onTouchEvent");
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.i("TOUCH TEST", "MyListView--->dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }
}
