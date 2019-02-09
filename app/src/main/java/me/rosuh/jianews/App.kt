package me.rosuh.jianews

import android.app.Application

import me.rosuh.android.jianews.R

/**
 * @author rosu
 * @date 2018/9/30
 */
class App : Application() {
    override fun onCreate() {
        setTheme(R.style.AppTheme)
        super.onCreate()
    }
}
