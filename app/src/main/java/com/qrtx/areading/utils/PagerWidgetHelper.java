package com.qrtx.areading.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.ViewGroup;

import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.ReadTheme;
import com.qrtx.areading.ui.view.readView.OnReadStateChangeListener;
import com.qrtx.areading.ui.view.readView.PageWidget;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 17-3-6.
 */

public class PagerWidgetHelper {
    private Context mContext;
    private boolean isReceived = false;
    private PageWidget mPageWidget;
    private ViewGroup mReadWidgetContainer;
    private IntentFilter sIntentFilter = new IntentFilter();//广播接收器的意图过滤器
    //小说全部章节集合
    private List<Book.Chapter> mChapterList;

    private Receiver mReceiver;//广播接收者
    private SimpleDateFormat mSdf = new SimpleDateFormat("HH:mm");

    private int mCurrentChapter = 0;
    //是否开始阅读章节
    private boolean mIsStartRead = false;

    private int[] mReadPos = new int[]{1, 0, 0};
    private Book mCurrentBook;
    private File[] mChapterFiles;


    private PagerWidgetHelper(Context ctx, ViewGroup readWidgetContainer, Book book) {
        sIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//电量变化　
        sIntentFilter.addAction(Intent.ACTION_TIME_TICK);//时间变化

        mReadWidgetContainer = readWidgetContainer;
        mCurrentBook = book;
        mChapterList = book.chapterList;
        mContext = ctx;
        mChapterFiles = BookUtil.getBookChapterFiles(mCurrentBook);

        if (!isReceived) {
            mReceiver = new Receiver();
            mContext.registerReceiver(mReceiver, sIntentFilter);
            isReceived = true;
        }

        LogUtil.e("BookTest", "mChapterFiles.length = " + mChapterFiles.length);
        initChapterList();
        LogUtil.e("BookTest", "mChapterList.size() = " + mChapterList.size());
        initPagerWidget();
    }

    private static PagerWidgetHelper mPagerWidgetHelper;

    public static PagerWidgetHelper init(Context ctx, ViewGroup readWidgetContainer, Book book) {
        if (mPagerWidgetHelper == null) {
            mPagerWidgetHelper = new PagerWidgetHelper(ctx, readWidgetContainer, book);
        }
        return mPagerWidgetHelper;
    }


    private void initPagerWidget() {
        mPageWidget = new PageWidget(mContext, mCurrentBook.bookID, mChapterList, new ReadListener());// 页面
        mReadWidgetContainer.removeAllViews();
        mReadWidgetContainer.addView(mPageWidget);
        //显示小说,当前数据是 内存里面的，合理的做法是 先判断本地 有没有缓存
        if (mChapterFiles.length > 0) {
            showChapterRead(mChapterList.get(0), 1);
        }
    }

    private File getChapterFile(String bookId, int chapter) {
        File file = FileUtils.getChapterFile(bookId, chapter);
        return file;
    }

    private Book.Chapter getChapterByPosition(int position) {
        File chapterFile = getChapterFile(mCurrentBook.bookID, position);
        return getChapterByFile(chapterFile);
    }


    private Book.Chapter getChapterByFile(File chapterFile) {
        String chapterId = MD5Util.MD5(chapterFile.getAbsolutePath());
        String fileName = chapterFile.getName();
        String chapterTitle = "第" + fileName.substring(0, fileName.lastIndexOf(".")) + "章";

        Book.Chapter chapter = new Book.Chapter(chapterId, chapterTitle, FileUtils.getTextByPath(chapterFile.getAbsolutePath()));

        return chapter;
    }


    // 加载章节内容
    private synchronized void showChapterRead(Book.Chapter data, int chapter) {
        if (data != null) {
            saveChapterFile(mCurrentBook.bookID, chapter, data);
        }

        if (!mIsStartRead) {
            mIsStartRead = true;
            if (!mPageWidget.isPrepared) {
                mPageWidget.init(ThemeManager.LEATHER);
                LogUtil.e("BookTest", "<---------jumpToChapter---------> mCurrentChapter = " + mCurrentChapter);
            } else {
                mPageWidget.jumpToChapter(mCurrentChapter);
            }
        }
    }

    //将章节内容缓存在本地
    public static void saveChapterFile(String bookId, int chapter, Book.Chapter data) {
        File file = FileUtils.getChapterFile(bookId, chapter);
        if (file.exists()) {//如果已经缓存　则不在缓存
            return;
        }
        FileUtils.writeFile(file.getAbsolutePath(), StringUtils.formatContent(data.body), false);
    }

    //将章节内容缓存在本地
    public static void saveChapterFile(String bookId, int chapter, Book.Chapter data, boolean isAppend) {
        File file = FileUtils.getChapterFile(bookId, chapter);
        FileUtils.writeFile(file.getAbsolutePath(), StringUtils.formatContent(data.body), isAppend);
    }

    private class ReadListener implements OnReadStateChangeListener {
        @Override
        public void onChapterChanged(int chapter) {//章节改变时回调

            mCurrentChapter = chapter;
            // 加载前一节 与 后三节  mChapterList.size()
            for (int i = chapter - 1; i <= chapter + 3 && i <= mChapterList.size(); i++) {

                //防止重复写入 浪费性能 ，读取缓存
                if (i > 0 && i != chapter && getChapterFile(mCurrentBook.bookID, i) == null) {
                    showChapterRead(getChapterByPosition(i), i);
                }
            }
        }

        @Override
        public void onPageChanged(int chapter, int page) {

        }

        @Override
        public void onLoadChapterFailure(int chapter) {

            mIsStartRead = false;

            ToastUtils.showToast("读取不到文件");
        }

        int theme = 0;

        @Override
        public void onCenterClick() {
            //被点击
            if (mListener != null) {
                mListener.onCenterClick();
//                setPageTheme((theme) % getReaderThemeList().size());
//                theme++;
            }
        }

        @Override
        public void onFlip() {
        }


        @Override
        public void onSaveReadProgress(String bookId, int chapter, int beginPos, int endPos) {
            Log.i("callback", "onSaveReadProgress : bookId:" + bookId + " chapter:" + chapter + " beginPos:" + beginPos + " endPos:" + endPos);

            mReadPos = new int[]{chapter, beginPos, endPos};
        }

        @Override
        public int[] onGetReadProgress(String bookId) {
            return mReadPos;
        }

    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPageWidget != null) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);

                    mPageWidget.setBattery(100 - level);
                } else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    mPageWidget.setTime(mSdf.format(new Date()));
                }
            }
        }
    }

    public void recycle() {

        if (isReceived && mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }


        mPageWidget.isPrepared = false;
        mIsStartRead = false;
        mPagerWidgetHelper = null;
    }

    /**
     * 只初始化50章内容　避免内存溢出
     *
     * @return
     */
    private List<Book.Chapter> initChapterList() {

        int count = mChapterFiles.length > 50 ? 50 : mChapterFiles.length;
        for (int i = 1; i <= count; i++) {
            Book.Chapter chapter = getChapterByPosition(i);
            mChapterList.add(chapter);
        }
        return mChapterList;
    }

    public void setPageTheme(int theme) {
        mPageWidget.setTheme(theme);
    }

    public void setFontSize(int fontSizePx) {
        mPageWidget.setFontSize(fontSizePx);
    }

    public void setTextColor(int textColor, int titleColor) {
        mPageWidget.setTextColor(textColor, titleColor);
    }

    public static List<ReadTheme> getReaderThemeList() {
        return ThemeManager.getReaderThemeData();
    }

    /**
     * 回调接口
     */
    public interface OnCenterClickListener {
        public void onCenterClick();
    }

    private OnCenterClickListener mListener;

    public void setOnCenterClickListener(OnCenterClickListener listener) {
        mListener = listener;
    }
}
