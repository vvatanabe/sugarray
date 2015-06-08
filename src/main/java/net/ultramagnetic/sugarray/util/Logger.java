package net.ultramagnetic.sugarray.util;

import android.os.Debug;
import android.util.Log;

import net.ultramagnetic.sugarray.BuildConfig;


public class Logger {

    private static final String TAG = Logger.class.getName();

    public static final void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static final void d(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    public static final void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static final void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, tr);
        }
    }

    public static final void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static final void i(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg, tr);
        }
    }

    public static final void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static final void v(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg, tr);
        }
    }

    public static final void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static final void w(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg, tr);
        }
    }

    public static final void heap() {
        heap(TAG);
    }

    public static final void heap(String tag) {
        if (BuildConfig.DEBUG) {
            String msg = "heap : Free="
                    + Long.toString(Debug.getNativeHeapFreeSize() / 1024)
                    + "kb" + ", Allocated="
                    + Long.toString(Debug.getNativeHeapAllocatedSize() / 1024)
                    + "kb" + ", Size="
                    + Long.toString(Debug.getNativeHeapSize() / 1024) + "kb";

            Log.v(tag, msg);
        }
    }
}
