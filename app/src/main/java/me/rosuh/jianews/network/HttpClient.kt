package me.rosuh.jianews.network

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
        builder
            .addInterceptor(HttpLoggingInterceptor().setLevel(BODY))
            .addInterceptor(AddCookiesInterceptor()) //这部分
            .addInterceptor(ReceivedCookiesInterceptor()) //这部分
    }
}