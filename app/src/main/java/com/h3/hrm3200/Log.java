package com.h3.hrm3200;

/**
 * Created by moonhyeonah on 2015. 4. 24..
 */
public class Log {
    public static boolean DEBUG = true;

    public static void i(String TAG, String log) {
        if (DEBUG) {
            android.util.Log.i(TAG, log);
        } else {
            // do nothing
        }
    }

    public static void v(String TAG, String log) {
        if (DEBUG) {
            android.util.Log.v(TAG, log);
        } else {
            // do nothing
        }
    }

    public static void d(String TAG, String log) {
        if (DEBUG) {
            android.util.Log.d(TAG, log);
        } else {
            // do nothing
        }
    }

    public static void w(String TAG, String log) {
        if (DEBUG) {
            android.util.Log.w(TAG, log);
        } else {
            // do nothing
        }
    }

    public static void e(String TAG, String log) {
        if (DEBUG) {
            android.util.Log.e(TAG, log);
        } else {
            // do nothing
        }
    }
}
