package me.rosuh.jianews.network

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import android.content.Context.MODE_PRIVATE
import android.R.id.edit
import android.content.SharedPreferences
import com.orhanobut.hawk.Hawk

/**
 *
 * @author rosuh
 * @date 2019/5/6
 */
class ReceivedCookiesInterceptor : Interceptor {

    override fun intercept(chain: Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            for (header in originalResponse.headers("Set-Cookie")) {
                Hawk.put("cookie", header)
            }
        }
        return originalResponse
    }
}