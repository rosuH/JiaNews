package me.rosuh.jianews.util

import android.content.Context
import android.content.Intent
import me.rosuh.jianews.user.Configure
import me.rosuh.jianews.view.LoginActivity
import java.lang.ref.WeakReference

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
object LoginUtil {
    @JvmStatic
    fun checkLogin(context: Context, callBack: LoginStatusCallBack) {
        // 弱引用，防止内存泄露，
        val reference: WeakReference<Context>? = WeakReference(context)
        if (Configure.USERID.isBlank()) { // 判断是否登录，否返回true
            Configure.CALLBACK = object : ILoginCallBack {
                override fun postExec() {
                    // 登录回调后执行登录回调前需要做的操作
                    if (Configure.USERID.isNotBlank()) {
                        // 这里需要再次判断是否登录，防止用户取消登录，取消则不执行登录成功需要执行的回调操作
                        callBack.logged()
                        //防止调用界面的回调方法中有传进上下文的引用导致内存泄漏
                        Configure.CALLBACK = null
                    }
                }
            }
            val mContext = reference!!.get()
            if (mContext != null) {
                val intent = Intent(mContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext.startActivity(intent)
                reference.clear()
            }
        } else {
            // 登录状态直接执行登录回调前需要做的操作
            callBack.logged()
        }
    }

    // 声明一个登录成功回调的接口
    interface ILoginCallBack {
        // 在登录操作及信息获取完成后调用这个方法来执行登录回调需要做的操作
        fun postExec()
    }

    interface LoginStatusCallBack {
        fun logged()
    }
}