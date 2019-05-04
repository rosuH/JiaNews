package me.rosuh.jianews.user

import android.content.Context
import com.orhanobut.hawk.Hawk
import me.rosuh.jianews.util.LoginUtil.ILoginCallBack



/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
class Configure {

    companion object {
        var USERID = ""
        var CALLBACK: ILoginCallBack? = null

        fun init(context: Context) {
            if (Hawk.contains("USERID")){
                Configure.USERID = Hawk.get("USERID")
                if (USERID.isNotBlank()){
                    getUserInfo()
                }
            }
        }

        private fun getUserInfo() {

        }
    }
}