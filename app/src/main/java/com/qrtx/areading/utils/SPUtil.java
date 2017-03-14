package com.qrtx.areading.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class SPUtil {

    private static SharedPreferences mSharedPreferences;
    private static Context mContext = AppUtil.getAppContext();
    private static String mFileName;

    private SPUtil() {
    }


    public void initSPUtil(String fileName) {
        mFileName = fileName;
    }

    /**
     * 获取一个以应用名称命名的SharedPreferences实例对象
     */
    private static SharedPreferences getSharedPreference() {

        if (mSharedPreferences == null) {

            if (mFileName != null) {
                String fileName = getAppName(mContext);
                getSharedPreference(fileName);
            } else {
                getSharedPreference(mFileName);
            }
        }
        return mSharedPreferences;
    }

    /**
     * 获取一个SharedPreferences实例对象
     *
     * @param fileName
     */
    private static SharedPreferences getSharedPreference(String fileName) {

        mSharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return mSharedPreferences;
    }

    /**
     * 保存一个String类型的值！
     */
    public static void putString(String key, String value) {

        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(key, value).apply();
    }

    /**
     * 获取String的value
     */
    public static String getString(String key, String defValue) {

        SharedPreferences sharedPreference = getSharedPreference();
        return sharedPreference.getString(key, defValue);
    }

    /**
     * 保存一个Boolean类型的值！
     */
    public static void putBoolean(String key, Boolean value) {

        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putBoolean(key, value).apply();
    }

    /**
     * 获取boolean的value
     */
    public static boolean getBoolean(String key, Boolean defValue) {

        SharedPreferences sharedPreference = getSharedPreference();
        return sharedPreference.getBoolean(key, defValue);
    }

    /**
     * 保存一个int类型的值！
     */
    public static void putInt(String key, int value) {

        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putInt(key, value).apply();
    }

    /**
     * 获取int的value
     */
    public static int getInt(String key, int defValue) {

        SharedPreferences sharedPreference = getSharedPreference();
        return sharedPreference.getInt(key, defValue);
    }

    /**
     * 保存一个float类型的值！
     */
    public static void putFloat(String fileName, String key, float value) {

        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putFloat(key, value).apply();
    }

    /**
     * 获取float的value
     */
    public static float getFloat(String key, Float defValue) {

        SharedPreferences sharedPreference = getSharedPreference();
        return sharedPreference.getFloat(key, defValue);
    }

    /**
     * 保存一个long类型的值！
     */
    public static void putLong(String key, long value) {

        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putLong(key, value).apply();
    }

    /**
     * 获取long的value
     */
    public static long getLong(String key, long defValue) {

        SharedPreferences sharedPreference = getSharedPreference();
        return sharedPreference.getLong(key, defValue);
    }

    /**
     * 清空对应key数据
     */
    public static void remove(String key) {

        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.remove(key).apply();
    }

    /**
     * 取出List<String>
     *
     * @param key List<String> 对应的key
     * @return List<String>
     */
    public static List<String> getStrListValue(String key) {

        List<String> strList = new ArrayList<String>();
        int size = getInt(key + "size", 0);
        // Log.d("sp", "" + size);
        for (int i = 0; i < size; i++) {
            strList.add(getString(key + i, null));
        }
        return strList;
    }

    /**
     * 存储List<String>
     *
     * @param context
     * @param key     List<String>对应的key
     * @param strList 对应需要存储的List<String>
     */
    public static void putStrListValue(String key, List<String> strList) {

        if (null == strList) {
            return;
        }
        // 保存之前先清理已经存在的数据，保证数据的唯一性
        removeStrList(key);
        int size = strList.size();
        putInt(key + "size", size);
        for (int i = 0; i < size; i++) {
            putString(key + i, strList.get(i));
        }
    }

    /**
     * 清空List<String>所有数据
     *
     * @param key List<String>对应的key
     */
    public static void removeStrList(String key) {

        int size = getInt(key + "size", 0);
        if (0 == size) {
            return;
        }
        remove(key + "size");
        for (int i = 0; i < size; i++) {
            remove(key + i);
        }
    }

    /**
     * 获取应用程序名称
     */
    private static String getAppName(Context context) {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
