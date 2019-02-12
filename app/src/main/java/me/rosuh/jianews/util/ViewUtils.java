package me.rosuh.jianews.util;

import android.content.Context;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author rosu
 * @date 2018/10/3
 */
public class ViewUtils {
    public static boolean isInMainThread(){
        return Thread.currentThread().equals(Looper.getMainLooper().getThread());
    }

    /**
     * 获得屏幕宽度像素
     * @param context
     * @return
     */
    public static int getScreenWidthInPixel(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenW = outMetrics.widthPixels;
        wm = null;
        return screenW;
    }

}
