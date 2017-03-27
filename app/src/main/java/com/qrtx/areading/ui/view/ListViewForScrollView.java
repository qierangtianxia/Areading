
package com.qrtx.areading.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewForScrollView extends ListView {

    public ListViewForScrollView(Context context) {
        super(context);
        initView();
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ListViewForScrollView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private void initView() {
        this.setSelector(new ColorDrawable());// 设置默认状态选择器为全透明
        //this.setDivider(null);// 去掉分隔线
        this.setCacheColorHint(Color.TRANSPARENT);// 有时候滑动listview背景会变成黑色,
        // 此方法将背景变为全透明
    }

}
