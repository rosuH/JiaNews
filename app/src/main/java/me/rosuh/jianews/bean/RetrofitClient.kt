package me.rosuh.jianews.bean

import android.support.annotation.Nullable
import me.rosuh.jianews.util.Const
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
object RetrofitClient {
    private val okHttpBuilder = HttpClient.builder
    init {
        if (Const.SERVER_BASE_IP.isEmpty()){
            throw Throwable("====================Please fill the server IP in Const.java====================")
        }
    }
    val retrofitBuilder = Retrofit.Builder()
        .baseUrl(Const.SERVER_BASE_IP)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpBuilder.build())
}