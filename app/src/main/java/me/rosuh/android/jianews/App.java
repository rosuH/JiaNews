package me.rosuh.android.jianews;

import android.app.Application;

/**
 * @author rosu
 * @date 2018/9/30
 */
public class App extends Application {
    @Override
    public void onCreate() {
        setTheme(R.style.AppTheme);
        super.onCreate();
    }
}
