package me.rosuh.android.jianews.util;

import android.os.Looper;

/**
 * @author rosu
 * @date 2018/10/3
 */
public class ViewUtils {
    public static boolean isInMainThread(){
        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }
}
