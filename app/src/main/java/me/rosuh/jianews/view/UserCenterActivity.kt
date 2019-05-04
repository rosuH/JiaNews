package me.rosuh.jianews.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.user_center_fragment.btn_login_or_logout
import kotlinx.android.synthetic.main.user_center_fragment.tv_account
import kotlinx.android.synthetic.main.user_center_fragment.tv_name
import me.rosuh.android.jianews.R
import me.rosuh.jianews.bean.User
import me.rosuh.jianews.network.ApiService
import me.rosuh.jianews.user.Configure

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
class UserCenterActivity: AppCompatActivity() {
    private var user:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_center_fragment)
        if (Hawk.contains("user")){
            user = Hawk.get<User>("user")?.run {
                tv_account.text = account
                tv_name.text = name
                this
            }
        }
        btn_login_or_logout.setOnClickListener {
            if (user != null){
                val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                pDialog.titleText = "正在注销"
                pDialog.setCancelable(true)

                val dis = ApiService.logout(user!!)
                    .doOnSubscribe {
                        pDialog.show()
                    }
                    .subscribe(
                        {
                            pDialog.titleText = "注销成功"
                            pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                            cleanUser()
                            btn_login_or_logout.text = getString(R.string.go_login)
                            btn_login_or_logout.setOnClickListener {
                                val intent = Intent(this, LoginActivity::class.java)
                                this.startActivity(intent)
                            }
                        },
                        {
                            pDialog.titleText = "注销成功"
                            pDialog.contentText = it.message
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                            pDialog.setOnDismissListener {
                                finish()
                            }
                        }
                    )
                pDialog.setCancelClickListener {
                    dis.dispose()
                }
            }
        }
    }

    private fun cleanUser() {
        if (Hawk.contains("user")){
            Hawk.delete("user")
        }
        if (Hawk.contains("USERID")){
            Hawk.delete("USERID")
            Configure.USERID = ""
        }

    }

    companion object {
        val instance: UserCenterActivity
            get() = UserCenterActivity()
        const val USER_CENTER_FRAGMENT_TAG = "UserCenterActivity"
    }
}