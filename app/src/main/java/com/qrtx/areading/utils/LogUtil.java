package com.qrtx.areading.utils;

import android.util.Log;

public class LogUtil {
	/**
	 * 默认Tag
	 */
	private static final String TAG = "TEST";

	/**
	 * 是否打印log
	 */
	private static final boolean DEBUG_KEY = true;

	/**
	 * 是否保存log
	 */
	private static final boolean SAVE_KEY = false;

	/**
	 * Priority constant for the println method; use Log.v.
	 */
	private static final int LEVEL_VERBOSE = 0;

	/**
	 * Priority constant for the println method; use Log.d.
	 */
	private static final int LEVEL_DEBUG = 1;

	/**
	 * Priority constant for the println method; use Log.i.
	 */
	private static final int LEVEL_INFO = 2;

	/**
	 * Priority constant for the println method; use Log.w.
	 */
	private static final int LEVEL_WARN = 3;

	/**
	 * Priority constant for the println method; use Log.e.
	 */
	private static final int LEVEL_ERROR = 4;

	/**
	 * 打印Log的级别
	 */
	private static int DEBUG_LEVEL = LEVEL_VERBOSE;

	private LogUtil() {
	}

	/*************************** 默认Tag **********************************/
	public static void v(String msg) {
		v(TAG, msg);
	}

	public static void d(String msg) {
		d(TAG, msg);
	}

	public static void i(String msg) {
		i(TAG, msg);
	}

	public static void w(String msg) {
		w(TAG, msg);
	}

	public static void e(String msg) {
		e(TAG, msg);
	}

	/*************************** 自定义Tag **********************************/
	public static void v(String tag, String msg) {
		saveLogToSDcard(msg);
		printLog(LEVEL_VERBOSE, tag, msg);
	}

	public static void d(String tag, String msg) {
		saveLogToSDcard(msg);
		printLog(LEVEL_DEBUG, tag, msg);
	}

	public static void i(String tag, String msg) {
		saveLogToSDcard(msg);
		printLog(LEVEL_INFO, tag, msg);
	}

	public static void w(String tag, String msg) {
		saveLogToSDcard(msg);
		printLog(LEVEL_WARN, tag, msg);
	}

	public static void e(String tag, String msg) {
		saveLogToSDcard(msg);
		printLog(LEVEL_ERROR, tag, msg);
	}

	private static void printLog(int debugLv, String tag, String msg) {
		if (!DEBUG_KEY) {
			return;
		}

		if (debugLv < DEBUG_LEVEL) {
			return;
		}

		switch (debugLv) {
		case LEVEL_VERBOSE:
			Log.v(tag, msg);
			break;
		case LEVEL_DEBUG:
			Log.d(tag, msg);
			break;
		case LEVEL_INFO:
			Log.i(tag, msg);
			break;
		case LEVEL_WARN:
			Log.w(tag, msg);
			break;
		case LEVEL_ERROR:
			Log.e(tag, msg);
			break;
		}
	}

	private static void saveLogToSDcard(String log) {
		if (!SAVE_KEY) {
			return;
		}

		// TODO 保存log
	}
}
