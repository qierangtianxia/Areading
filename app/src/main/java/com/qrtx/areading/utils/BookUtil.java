package com.qrtx.areading.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.qrtx.areading.Constants;
import com.qrtx.areading.beans.Book;
import com.qrtx.areading.beans.BookMark;
import com.qrtx.areading.net.API;
import com.qrtx.areading.net.SimpleCommonCallback;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qrtx.areading.utils.BookUtil.addBookToNovel;


public class BookUtil {


    //从本地文件提取小说章节信息
    public static int clipBookChapterListFroLocal(Book book) throws IOException {
        String bookPath = book.getBookPath();
        LogUtil.i("clipBookChapterListFroLocal    --> bookPath　＝　" + bookPath);
        File file = new File(bookPath);
        if (!file.exists()) {
            return 0;
        }
        File chapterDir = new File(Constants.PATH_BOOK_BASE + File.separator + book.bookID + File.separator);
        if (!chapterDir.exists()) {
            chapterDir.mkdirs();
        } else {
            return 0;
        }
        String regex = "(第.{1,7}章)";
        Pattern pattern = Pattern.compile(regex);

        int chapterCount = 1;
        String readline;
        String chapterBasePath = chapterDir.getAbsolutePath() + File.separator;

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)));

        int readFirstTime = 0;
        String path = chapterBasePath + chapterCount + ".txt";
        //一次读取一行
        while ((readline = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(readline);


            if (!matcher.find()) {//没有匹配到章节关键字
                FileUtils.writeFile(path, readline, true);
                continue;
            }
            LogUtil.e("找到章节　" + matcher.group());

            //如果匹配到章节关键字，将章节关键字之前的内容写入上一章
            String content = readline.substring(0,
                    readline.indexOf(matcher.group()));
            FileUtils.writeFile(path, content, true);

//            if (chapterCount >= 10) {//设置最多查找100章
//                break;
//            }
            readFirstTime++;
            if (readFirstTime != 1) {
                chapterCount++;
            }
            //然后进入下一章内容的读取
            readline.substring(readline.indexOf(matcher.group()));
            content = readline.substring(readline.indexOf(matcher.group()));
            path = chapterBasePath + chapterCount + ".txt";
            FileUtils.writeFile(path, content, true);
        }
        LogUtil.e("chapterCount = " + chapterCount);
        return chapterCount;
    }

    //从数据库提取小说章节信息
    private static ArrayList<Book.Chapter> getBookChapterListForDB(String bookID) {
        return null;
    }


    public static Book obtionBook(String bookPath) {

        LogUtil.i("bookPath = " + bookPath);
        String bookName = bookPath.substring(bookPath.lastIndexOf("/") + 1,
                bookPath.lastIndexOf(".txt"));
        String bookId = MD5Util.MD5(bookPath);
        final Book book = new Book(bookId, bookName, null);
        book.setBookPath(bookPath);

        LogUtil.d("obtion book", "obtion book -----> bookname = " + bookName + "     bookId = " + bookId);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    clipBookChapterListFroLocal(book);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return book;
    }

    /**
     * 插入书籍到数据库
     */
    public static long insertBookToDb(Book book) {
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        String tableName = Constants.TABLE_NAME_BOOK_NOVEL;
        String sql = "create table if not exists " + tableName + "(_id integer primary key autoincrement,bookId varchar(20),bookName varchar(20),bookPath varchar(20))";
        database.execSQL(sql);

        ContentValues values = new ContentValues();
        values.put("bookId", book.bookID);
        values.put("bookName", book.getBookName());
        values.put("bookPath", book.getBookPath());

        return database.insert(tableName, null, values);
    }

    public static ArrayList<Book> getBookFromNovelDb() {
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        String tableName = "novelTavle";


        String sql = "create table if not exists " + tableName + "(_id integer primary key autoincrement,bookId varchar(20),bookName varchar(20),bookPath varchar(20))";
        database.execSQL(sql);

        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor != null || cursor.getColumnCount() > 0) {
            ArrayList<Book> books = new ArrayList<>();
            while (cursor.moveToNext()) {
                String bookPath = cursor.getString(cursor.getColumnIndex("bookPath"));
                if (bookPath.endsWith(".txt")) {//本地书籍
                    books.add(BookUtil.obtionBook(bookPath));
                } else {//在线书籍
                    String bookName = cursor.getString(cursor.getColumnIndex("bookName"));
                    String bookId = cursor.getString(cursor.getColumnIndex("bookId"));
                    books.add(new Book(bookId, bookName, null));
                }
                LogUtil.i("从数据库读取书籍成功!");
            }
            return books;
        }
        cursor.close();
        database.close();
        return null;
    }


    /**
     * 返回书籍的章节个数　　前提是已经对该书进行章节分割
     *
     * @param book
     * @return
     */
    public static File[] getBookChapterFiles(Book book) {
        if (book == null) {
            return null;
        }
        String bookPath = book.getBookPath();
        //TODO 需要优化　代码重复
        if (bookPath == null) {
            String chaptersPath = Constants.PATH_BOOK_BASE + File.separator + book.bookID;
            File chapterDir = new File(chaptersPath);
            File[] list = chapterDir.listFiles();
            return list;
        }
        File file = new File(bookPath);
        if (!file.exists()) {
            return null;
        }

        String chaptersPath = Constants.PATH_BOOK_BASE + File.separator + book.bookID;
        File chapterDir = new File(chaptersPath);
        File[] list = chapterDir.listFiles();
        return list;
    }

    public static ArrayList<String> getBookIDsFromNovel() {
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        String tableName = Constants.TABLE_NAME_BOOK_NOVEL;
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor != null && cursor.getColumnCount() > 0) {
            ArrayList<String> bookIDs = new ArrayList<>();
            while (cursor.moveToNext()) {
                String bookID = cursor.getString(cursor.getColumnIndex("bookId"));
                bookIDs.add(bookID);
            }
            cursor.close();
            return bookIDs;
        }
        cursor.close();
        return null;
    }


    public static void addBookToNovel(Book book) {
        if (hasAddToNovel(book)) {
            return;
        }
        long count = BookUtil.insertBookToDb(book);
        if (count > 0) {
            GlobalController.sNovelIsUpdata = true;
            ToastUtils.showToast("添加成功！");
        }
    }

    /**
     * 是否已经加入书架
     *
     * @param book
     * @return
     */
    public static boolean hasAddToNovel(Book book) {
        ArrayList<String> bookIDsFromNovel = BookUtil.getBookIDsFromNovel();
        if (bookIDsFromNovel != null && bookIDsFromNovel.contains(book.bookID)) {//如果已经在书架，则不添加
            return true;
        }
        return false;
    }

    public static boolean hasAddToNovel(String bookPath) {
        ArrayList<String> bookIDsFromNovel = BookUtil.getBookIDsFromNovel();
        String bookId = MD5Util.MD5(bookPath);
        if (bookIDsFromNovel != null && bookIDsFromNovel.contains(bookId)) {//如果已经在书架，则不添加
            return true;
        }
        return false;
    }

    /**
     * 是否已经换存在本地
     *
     * @param book
     * @return
     */
    public static boolean hasSaveToLocal(Book book) {
        String bookPath = Constants.PATH_BOOK_BASE + File.separator + book.bookID;
        LogUtil.i("bookPath = " + bookPath);
        File bookDir = new File(bookPath);
        if (bookDir.exists()) {
            if (book.getBookPath() == null) {
                book.setBookPath(bookPath);
            }
            return true;
        }
        return false;
    }

    /**
     * 从本地移除
     *
     * @param book
     */
    public static void removeBookFromLocal(Book book) {
        if (!hasSaveToLocal(book)) {

            return;
        }
        File bookDir = new File(book.getBookPath());
        bookDir.delete();
    }


    public static void addBookToHistory(Book book) {

        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", book.bookID);
        contentValues.put("bookName", book.getBookName());
        contentValues.put("bookPath", book.getBookPath());
        contentValues.put("readTime", "" + System.currentTimeMillis());
        long insert = database.insert(Constants.TABLE_NAME_READ_HISTORY, null, contentValues);
        if (insert > 0) {
            LogUtil.i("加入历史记录成功--->　　book.getBookName()　＝　" + book.getBookName());
        }
    }

    public static void saveReadProgress(String bookId, int[] mReadPos) {
        if (mReadPos.length < 3) {
            throw new IllegalArgumentException("int　数组参数不合法");
        }
        SPUtil.putInt(bookId + Constants.KEY_READ_CHAPTER, mReadPos[0]);
        SPUtil.putInt(bookId + Constants.KEY_READ_POS_START, mReadPos[1]);
        SPUtil.putInt(bookId + Constants.KEY_READ_POS_END, mReadPos[2]);

        LogUtil.i("saveReadProgress", "--------onSaveReadProgress--------  ");
        LogUtil.i("saveReadProgress", "--------chapter--------  " + mReadPos[0]);
        LogUtil.i("saveReadProgress", "--------beginPos--------  " + mReadPos[1]);
        LogUtil.i("saveReadProgress", "--------endPos--------  " + mReadPos[2]);
    }

    public static int[] getReadProgress(String bookId) {
        int[] readPro = new int[3];
        readPro[0] = SPUtil.getInt(bookId + Constants.KEY_READ_CHAPTER, 1);
        readPro[1] = SPUtil.getInt(bookId + Constants.KEY_READ_POS_START, 0);
        readPro[2] = SPUtil.getInt(bookId + Constants.KEY_READ_POS_END, 0);

        LogUtil.i("getReadProgress", "--------getReadProgress--------  ");
        LogUtil.i("getReadProgress", "--------chapter--------  " + readPro[0]);
        LogUtil.i("getReadProgress", "--------beginPos--------  " + readPro[1]);
        LogUtil.i("getReadProgress", "--------endPos--------  " + readPro[2]);
        return readPro;
    }

    /**
     * 保存书签
     *
     * @param bookMark
     */
    public static void markBook(BookMark bookMark) {

        if (hasAddMarkBook(bookMark)) {//如果已经加入书签，则不在加入
            ToastUtils.showToast("当前页面已经加入书签！");
            return;
        }
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        String tableName = Constants.TABLE_NAME_BOOK_MARK + bookMark.getBookID();

        ContentValues values = new ContentValues();
        values.put("bookId", bookMark.getBookID());
        values.put("percentStr", bookMark.getPercentStr());
        values.put("time", bookMark.getTime());
        values.put("chapterName", bookMark.getChapterName());
        values.put("markId", bookMark.getMarkId());
        int[] progress = bookMark.getProgress();
        values.put("chapterNo", progress[0]);
        values.put("startPos", progress[1]);
        values.put("endPos", progress[2]);
        long insert = database.insert(tableName, null, values);

        if (insert > 0) {
            ToastUtils.showToast("书签添加成功");
        }

        database.close();
    }


    public static int removeBookMark(BookMark bookMark) {
        if (!hasAddMarkBook(bookMark)) {//如果没有加入书签，则返回不在向下执行
            return -1;
        }
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        String tableName = Constants.TABLE_NAME_BOOK_MARK + bookMark.getBookID();

        int delete = database.delete(tableName, "markId=?", new String[]{bookMark.getMarkId()});
        database.close();
        if (delete > 0) {
            ToastUtils.showToast("删除成功");
        }
        return delete;
    }


    public static boolean hasAddMarkBook(BookMark bookMark) {
        List<BookMark> bookMarks = getBookMark(bookMark.getBookID());
        return bookMarks.contains(bookMark);
    }

    public static List<BookMark> getBookMark(String bookId) {
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        String tableName = Constants.TABLE_NAME_BOOK_MARK + bookId;
        String sql = "create table if not exists " + tableName + "(_id integer primary key autoincrement,bookId varchar(20),markId varchar(20),chapterName varchar(20),time varchar(20),percentStr varchar(20),chapterNo integer,startPos integer,endPos integer)";
        database.execSQL(sql);

        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        List<BookMark> bookMarkList = null;
        if (cursor != null && cursor.getColumnCount() > 0) {
            bookMarkList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String bookID = cursor.getString(cursor.getColumnIndex("bookId"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String percentStr = cursor.getString(cursor.getColumnIndex("percentStr"));
                String markId = cursor.getString(cursor.getColumnIndex("markId"));
                String chapterName = cursor.getString(cursor.getColumnIndex("chapterName"));
                int[] progress = new int[3];
                progress[0] = cursor.getInt(cursor.getColumnIndex("chapterNo"));
                progress[1] = cursor.getInt(cursor.getColumnIndex("startPos"));
                progress[2] = cursor.getInt(cursor.getColumnIndex("endPos"));

                BookMark bookMark = new BookMark(markId, chapterName, percentStr, time, bookID, progress);
                bookMarkList.add(bookMark);
            }
        }

        cursor.close();
        database.close();
        return bookMarkList;
    }
}