package com.qrtx.areading.beans;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 17-3-2.
 */

public class Book implements Serializable {
    public ArrayList<Chapter> chapterList;//章节列表集合
    public String bookID;
    private String bookName;
    private String author;
    private Bitmap bookIcon;
    private String bookPath;
    private String summary;

    public Book(String bookID, String bookName, Bitmap bookIcon) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.bookIcon = bookIcon;
        chapterList = new ArrayList<>();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public Bitmap getBookIcon() {
        return bookIcon;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBookIcon(Bitmap bookIcon) {
        this.bookIcon = bookIcon;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//    }

    public static class Chapter implements Serializable {

        public Chapter(String id, String title, String body) {
            this.title = title;
            this.id = id;
            this.body = body;
        }

        public String title;
        public String path;
        public String id;
        public String body;
    }

    @Override
    public boolean equals(Object obj) {
        Book b = (Book) obj;
        return this.bookID.equals(b.bookID);
    }
}
