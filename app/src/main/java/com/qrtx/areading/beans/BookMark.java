package com.qrtx.areading.beans;

import java.text.SimpleDateFormat;

/**
 * Created by user on 17-3-22.
 */

public class BookMark {
    private String markId;
    private String time;
    private String percentStr;
    private String bookID;
    private String chapterName;
    private int[] progress;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public BookMark(String id, String chapterName, String percentStr, long time, String bookID, int[] progress) {
        this.markId = id;
        this.time = format.format(time);
        this.chapterName = chapterName;
        this.bookID = bookID;
        this.progress = progress;
        this.percentStr = percentStr;
    }

    public BookMark(String id, String chapterName, String percentStr, String time, String bookID, int[] progress) {
        this.markId = id;
        this.chapterName = chapterName;
        this.time = time;
        this.bookID = bookID;
        this.progress = progress;
        this.percentStr = percentStr;
    }

    public String getMarkId() {
        return markId;
    }

    public String getTime() {
        return time;
    }

    public String getBookID() {
        return bookID;
    }

    public int[] getProgress() {
        return progress;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getPercentStr() {
        return percentStr;
    }

    @Override
    public boolean equals(Object obj) {
        BookMark bm = (BookMark) obj;
        return chapterName.equals(bm.getChapterName()) && percentStr.equals(bm.getPercentStr());
    }
}
