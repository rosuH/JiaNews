package me.rosuh.jianews.network


import android.content.Context
import me.rosuh.android.jianews.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import me.rosuh.jianews.App

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
object HttpClient {
    val builder = OkHttpClient.Builder()
    init {
        if (BuildConfig.DEBUG){
            builder
                .addInterceptor(HttpLoggingInterceptor().setLevel(BODY))
                .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(
                    App.instance().applicationContext
                )))
        }
    }
}