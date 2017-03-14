package com.qrtx.areading.ui.view;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.qrtx.areading.utils.LogUtil;

public class BookPagerView extends View {

    private static final int TEXT_SIZE = 20;
    private static final int H_JIANJU = 10;
    private static final int V_JIANJU = 10;
    private String mBookPath;

    public static boolean haseNextPager = true;

    public static int mStopPos;//动态全局断点
    public static ArrayList<Integer> mStopPosList;//记录每个断点
    private BufferedReader mBookReader;
    private Paint paint;
    private char[] mTextcontentChars;

    public BookPagerView(Context context, AttributeSet attrs, int defStyleAttr)
            throws FileNotFoundException {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setTextSize(30);
        paint.setAntiAlias(true);


        if (mStopPosList == null) {
            mStopPosList = new ArrayList<>();
        }
    }

    public BookPagerView(Context context, AttributeSet attrs)
            throws FileNotFoundException {
        this(context, attrs, 0);
    }

    public BookPagerView(Context context) throws FileNotFoundException {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextcontentChars == null) {
            return;
        }
        char[] chars = mTextcontentChars;
        int startPosX = 10, startPosY = 50;

        for (int i = mStopPos; i < chars.length; i++) {
            String str = chars[i] + "";
            float width = paint.measureText(str);

            if (startPosX + width >= getWidth()) {
                startPosY += 50;
                startPosX = 10;
            }
            if (startPosY > getHeight()) {
                mStopPos = i;
                mStopPosList.add(i);
                if (i > chars.length) {
                    haseNextPager = false;
                }
                return;
            }
            canvas.drawText(str, startPosX, startPosY, paint);
            startPosX += width;
        }
    }

    public void setContent(String text) {
        setContent(text.toCharArray());
    }

    public void setContent(char[] text) {
        mTextcontentChars = text;
        invalidate();
    }
}
