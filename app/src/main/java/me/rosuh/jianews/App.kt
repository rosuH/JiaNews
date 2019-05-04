package me.rosuh.jianews

import android.app.Application
import com.orhanobut.hawk.Hawk
import me.rosuh.android.jianews.R
import me.rosuh.jianews.user.Configure

/**
 * @author rosu
 * @date 2018/9/30
 */
class App : Application() {
    companion object {
        private lateinit var instance:App
        fun instance() = instance
    }

    override fun onCreate() {
        setTheme(R.style.AppTheme)
        super.onCreate()
        instance = this
        Hawk.init(instance.applicationContext).build()
        if (Hawk.contains("USERID")){
            Configure.USERID = Hawk.get<String>("USERID")
        }
    }
}
