package com.qrtx.areading.ui.view.readView;

/**
 * @author yuyh.
 * @date 2016/9/21.
 */
public interface OnReadStateChangeListener {

    void onChapterChanged(int chapter);

    void onPageChanged(int chapter, int page);

    void onLoadChapterFailure(int chapter);

    void onCenterClick();

    void onFlip();

    void onSaveReadProgress(String bookId, int chapter, int beginPos, int endPos);

    //上次阅读位置
    int[] onGetReadProgress(String bookId);
}
