package me.rosuh.jianews.network

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import android.content.Context.MODE_PRIVATE
import com.orhanobut.hawk.Hawk

/**
 *
 * @author rosuh
 * @date 2019/5/6
 */
class AddCookiesInterceptor : Interceptor {

    override fun intercept(chain: Chain): Response {
        val builder = chain.request().newBuilder()
        if (Hawk.contains("cookie")){
            val cookie = Hawk.get<String>("cookie")
            builder.addHeader("Cookie", cookie)
            print(builder.addHeader("Cookie", cookie))
        }
        return chain.proceed(builder.build())
    }
}