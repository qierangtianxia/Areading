package com.qrtx.areading.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.qrtx.areading.Constants;
import com.qrtx.areading.beans.Book;

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


public class BookUtil {

    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Areading";

    public static ArrayList<Book> getBookListFromSDcard() throws IOException {
        if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
            return null;
        }

        File baseFile = new File(BASE_PATH);
        if (!baseFile.exists()) {
            return null;
        }
        File[] files = baseFile.listFiles();
        if (files.length <= 0) {
            return null;
        }


        ArrayList<Book> books = new ArrayList<>();
        for (File file : files) {
            if (!file.getName().endsWith(".txt")) {
                continue;
            }
            String str = file.getName();
            String bookName = str.substring(0, str.lastIndexOf("."));

            Book book = new Book(bookName, bookName, null);
            book.setBookPath(file.getAbsolutePath());
            books.add(book);
        }
        return books;
    }

    public static ArrayList<Book.Chapter> getBookChapterList(Book book) throws IOException {
        if (book == null) {
            throw new NullPointerException(Book.class.getSimpleName() + " is null");
        }


        //从数据库读取章节信息
        book.chapterList = getBookChapterListForDB(book.bookID);
        if (book.chapterList == null) {//获取失败　
            //从本地文件提取小说章节信息
//            getBookChapterListFroLocal(book);
        }


        return book.chapterList;
    }

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

        int chapterCount = 0;
        String readline;
        String chapterBasePath = chapterDir.getAbsolutePath() + File.separator;

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)));

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

            if (chapterCount >= 5) {//设置最多查找10章
                break;
            }

            //然后进入下一章内容的读取
            chapterCount++;
            readline.substring(readline.indexOf(matcher.group()));
            content = readline.substring(readline.indexOf(matcher.group()));
            path = chapterBasePath + chapterCount + ".txt";
            FileUtils.writeFile(path, content, true);
        }

        path = chapterBasePath + 0 + ".txt";
        File firstFile = new File(path);
        if (firstFile.length() > 0) {
            if (chapterCount > 0) {//有第一章\\
                String chapter_1Path = chapterBasePath + "1.txt";
                String capter_0 = FileUtils.getTextByPath(path) + "\n";
                String capter_1 = FileUtils.getTextByPath(chapter_1Path);
                FileUtils.writeFile(chapter_1Path, capter_0 + capter_1, false);
            } else {//没有第一章
                String chapter_1Path = chapterBasePath + "1.txt";
                String capter_0 = FileUtils.getTextByPath(path) + "\n";
                FileUtils.writeFile(chapter_1Path, capter_0, false);
            }
        }
        firstFile.delete();
        LogUtil.e("chapterCount = " + chapterCount);
        return chapterCount;
    }

    //从数据库提取小说章节信息
    private static ArrayList<Book.Chapter> getBookChapterListForDB(String bookID) {
        return null;
    }


    public static Book obtionBook(String bookPath) {

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
    public static void insertBookToDb(Book book) {
        SQLiteDatabase database = DbUtil.openDatabase(Constants.DATABASE_NAME);
        String tableName = Constants.TABLE_NAME_BOOK_NOVEL;
        String sql = "create table if not exists " + tableName + "(_id integer primary key autoincrement,bookId varchar(20),bookName varchar(20),bookPath varchar(20))";
        database.execSQL(sql);

        ContentValues values = new ContentValues();
        values.put("bookId", book.bookID);
        values.put("bookName", book.getBookName());
        values.put("bookPath", book.getBookPath());

        database.insert(tableName, null, values);
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
                books.add(BookUtil.obtionBook(bookPath));
                LogUtil.i("从数据库读取书籍成功!");
            }
            return books;
        }
        cursor.close();
        return null;
    }


    public List<String> cut(String srcFilePath, String dstDir) {
        List<String> mFilePath = new ArrayList<>();
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(srcFilePath));
            String tempString = null;
            StringBuffer sb = new StringBuffer();
            Pattern p = Pattern.compile("第.*?章");
            Matcher m = null;
            int index = 0;
            int i = 0;
            File file;
            while ((tempString = br.readLine()) != null) {
                tempString += "\r\n";
                sb.append(tempString);
            }

            m = p.matcher(sb);
            while (m.find()) {
                file = new File(dstDir + "第" + i + "章.txt");
                bw = new BufferedWriter(new FileWriter(file));
                int start = m.start();
                if (m.find()) {
                    index = m.start();
                    bw.write(sb.toString(), start, index - start);

                    bw.flush();
                    m.region(index, sb.length());
                } else {
                    bw.write(sb.toString(), start, sb.length() - start);
                }
                mFilePath.add(file.getAbsolutePath());
                i++;
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mFilePath;
    }

    /**
     * 返回书籍的章节个数　　前提是已经对该书进行章节分割
     *
     * @param book
     * @return
     */
    public static File[] getBookChapterFiles(Book book) {
        if (book == null) {
            throw new NullPointerException();
        }
        File file = new File(book.getBookPath());
        if (!file.exists()) {
            throw new NullPointerException();
        }

        String chaptersPath = Constants.PATH_BOOK_BASE + File.separator + book.bookID;
        LogUtil.e("");
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

}