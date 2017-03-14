package com.qrtx.areading.test;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qrtx.areading.utils.LogUtil;

/**
 * Created by user on 17-3-10.
 */

public class MyDreawLayout extends DrawerLayout {
    public MyDreawLayout(Context context) {
        super(context);
    }

    public MyDreawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDreawLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LogUtil.i("TOUCH TEST", "MyDreawLayout--->onTouchEvent");
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.i("TOUCH TEST", "MyDreawLayout--->dispatchTouchEvent");
        if (isDrawerOpen(GravityCompat.START)) {
            LogUtil.e("TOUCH TEST", "00000000000000000000");
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
