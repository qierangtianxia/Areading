package com.qrtx.areading.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qrtx.areading.Constants;
import com.qrtx.areading.R;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.BookMark;
import com.qrtx.areading.ui.adapter.ChapterlvAdapter;
import com.qrtx.areading.ui.view.readView.PageFactory;
import com.qrtx.areading.utils.BookUtil;
import com.qrtx.areading.utils.FileUtils;
import com.qrtx.areading.utils.LogUtil;
import com.qrtx.areading.utils.OnKeyShareUtil;
import com.qrtx.areading.utils.PagerWidgetHelper;
import com.qrtx.areading.utils.ToastUtils;

import java.io.File;

public class ReadActivity extends Activity implements View.OnClickListener {
    private TextView mTvCatalog, mTvTextFont, mTvPagerTheme, mTvBookMark, mTvNext, mTvLast;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mLlChapter;
    private PagerWidgetHelper mPagerWidgetHelper;
    private FrameLayout mFrameLayout;
    private ListView mLvChapter;
    private Book mCurrentBook;
    private SeekBar mSeekBar;
    private View mSettingViewCeil, mSettingViewBottom;
    private ImageButton mBtnBack, mBtnMore;
    private ImageButton mBtnBookMark;
    private String[] mStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        initView();

        initEvent();

    }


    @Override
    protected void onStart() {
        super.onStart();
        BookUtil.addBookToHistory(mCurrentBook);
    }

    private void initEvent() {

        mTvLast.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mTvBookMark.setOnClickListener(this);
        mBtnMore.setOnClickListener(this);
        mBtnBookMark.setOnClickListener(this);


        mPagerWidgetHelper.setOnPagerListener(new PagerWidgetHelper.onPagerListener() {
            @Override
            public void onCenterClick() {
                LogUtil.i("onCenterClick");
                if (mSettingViewCeil.getVisibility() == View.GONE) {
                    showMenu();
                } else {
                    hideMenu();
                }
            }

            @Override
            public void onFlip() {
                if (mSettingViewCeil.getVisibility() == View.VISIBLE) {
                    hideMenu();
                }
            }

            @Override
            public void onPageChanged(int chapter) {
                mSeekBar.setProgress(chapter - 1);
            }
        });

        mTvCatalog.setOnClickListener(this);
        mTvTextFont.setOnClickListener(this);
        mTvPagerTheme.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPagerWidgetHelper.jumpToChapter(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void showMenu() {
        mSettingViewCeil.setVisibility(View.VISIBLE);
        mSettingViewBottom.setVisibility(View.VISIBLE);
    }

    private void hideMenu() {
        mSettingViewCeil.setVisibility(View.GONE);
        mSettingViewBottom.setVisibility(View.GONE);
    }

    private void initView() {
        mLlChapter = (LinearLayout) findViewById(R.id.id_chapter_ll);
        mBtnBack = (ImageButton) findViewById(R.id.id_ibtn_back);
        mBtnMore = (ImageButton) findViewById(R.id.id_btn_more);
        mBtnBookMark = (ImageButton) findViewById(R.id.id_btn_mark_book);
        mTvCatalog = (TextView) findViewById(R.id.id_tv_catalog);
        mTvBookMark = (TextView) findViewById(R.id.id_tv_bookmark);
        mTvTextFont = (TextView) findViewById(R.id.id_tv_textfont);
        mTvNext = (TextView) findViewById(R.id.id_tv_next_chapter);
        mTvLast = (TextView) findViewById(R.id.id_tv_last_chapter);
        mTvPagerTheme = (TextView) findViewById(R.id.id_tv_pagertheme);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout_read);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        mFrameLayout = (FrameLayout) findViewById(R.id.flReadWidget);
        mCurrentBook = (Book) getIntent().getSerializableExtra(Constants.KEY_BOOK);
        mSettingViewCeil = findViewById(R.id.id_read_setting_ceil);
        mLvChapter = (ListView) findViewById(R.id.id_lv_chapterlist);
        mSettingViewBottom = findViewById(R.id.id_read_setting_bottom);

        mPagerWidgetHelper = PagerWidgetHelper.init(ReadActivity.this, mFrameLayout, mCurrentBook);


        initChapterList();

        mSeekBar.setMax(mStrings.length);
    }

    private void initChapterList() {
        String bookPath = FileUtils.getBookPath(mCurrentBook.bookID);
        File bookFile = new File(bookPath);
        mStrings = bookFile.list();
        LogUtil.i("mStrings.length = " + mStrings.length);
        LogUtil.i("bookPath = " + bookPath);
        if (mStrings == null || mStrings.length <= 0) {
            return;
        }
        mLvChapter.setAdapter(new ChapterlvAdapter(this, mStrings));
        mLvChapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position + 1 > mStrings.length) {
                    return;
                }
                mPagerWidgetHelper.jumpToChapter(position + 1);
                mDrawerLayout.closeDrawers();
                hideMenu();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPagerWidgetHelper.recycle();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (mSettingViewCeil.getVisibility() == View.VISIBLE) {
            hideMenu();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_catalog://打开章节菜单列表
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.id_tv_textfont://调整字体大小
                mPagerWidgetHelper.setFontSizeRandom();
                break;
            case R.id.id_tv_pagertheme://调整页面主题
                mPagerWidgetHelper.setPageThemeRandom();
//                mLlChapter.setBackgroundColor(ThemeManager.getCurrentThemeColor());
                break;
            case R.id.id_ibtn_back://返回
                finish();
                break;
            case R.id.id_btn_mark_book://添加书签
                addBookMark();
                break;
            case R.id.id_tv_bookmark://查看书签
                showBookMarkView();
                hideMenu();
                break;
            case R.id.id_btn_more://更多　　分享
                OnKeyShareUtil.showShare("快来看书啦", "《" + mCurrentBook.getBookName() + "》好看极了，快来一起看书啊！");
                break;
            case R.id.id_tv_next_chapter://下一章
                int currentChapter = mPagerWidgetHelper.getCurrentChapter();
                if (currentChapter + 1 > mStrings.length) {
                    ToastUtils.showToast("没有下一章了！");
                } else {
                    mPagerWidgetHelper.jumpToChapter(currentChapter + 1);
                }
                break;
            case R.id.id_tv_last_chapter://上一章
                currentChapter = mPagerWidgetHelper.getCurrentChapter();
                if (currentChapter - 1 < 1) {
                    ToastUtils.showToast("没有上一章了！");
                } else {
                    mPagerWidgetHelper.jumpToChapter(currentChapter - 1);
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CODE_SHOW_BOOKMARK) {
            int[] readPro = data.getIntArrayExtra(Constants.KEY_READ_BOOK_MARK);
            mPagerWidgetHelper.jumpToChapter(readPro);
        }
    }

    private void addBookMark() {
        int[] currentPro = mPagerWidgetHelper.getCurrentPro();
        String chapterName = mStrings[currentPro[0] - 1];
        BookMark bookMark = new BookMark("" + System.currentTimeMillis(), chapterName, PageFactory.mPercentStr, System.currentTimeMillis(), mCurrentBook.bookID, currentPro);
        BookUtil.markBook(bookMark);
    }

    private void showBookMarkView() {
        Intent intent = new Intent(this, BookMarkListActivity.class);
        intent.putExtra(Constants.KEY_USER_BOOK_ID, mCurrentBook.bookID);

        startActivityForResult(intent, Constants.REQUEST_CODE_SHOW_BOOKMARK);
    }
}
