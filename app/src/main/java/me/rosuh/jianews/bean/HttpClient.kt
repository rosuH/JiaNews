package me.rosuh.jianews.bean


import me.rosuh.android.jianews.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
object HttpClient {
    val builder = OkHttpClient.Builder()
    init {
        if (BuildConfig.DEBUG){
            builder.addInterceptor(
                HttpLoggingInterceptor().setLevel(BODY)
            )
        }
    }
}