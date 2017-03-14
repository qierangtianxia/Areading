package com.qrtx.areading.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qrtx.areading.Constants;

import java.util.HashMap;

/**
 * Created by user on 17-3-5.
 */

public class DbUtil {


    private static HashMap<String, SQLiteOpenHelper> databaseHelperMap;

    public static SQLiteDatabase openDatabase(String name) {

        if (databaseHelperMap == null) {
            databaseHelperMap = new HashMap<>();
        }
        SQLiteOpenHelper databaseHelper;
        if (databaseHelperMap.keySet().contains(name)) {
            databaseHelper = databaseHelperMap.get(name);
        } else {
            databaseHelper = new DatabaseHelper(name);
        }
        return databaseHelper.getWritableDatabase();
    }

    public static void execSQL(SQLiteDatabase database, String sqlStr) {
        database.execSQL(sqlStr);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(String name) {
            super(AppUtil.getAppContext(), name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + Constants.TABLE_NAME_READ_HISTORY + "(_id integer,bookName varchar(20))";
            execSQL(db, sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
