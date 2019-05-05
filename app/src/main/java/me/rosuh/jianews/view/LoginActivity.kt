package me.rosuh.jianews.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.login_activity.btn_go_register
import kotlinx.android.synthetic.main.login_activity.btn_login_or_register
import kotlinx.android.synthetic.main.login_activity.register_group
import kotlinx.android.synthetic.main.login_activity.ti_account
import kotlinx.android.synthetic.main.login_activity.ti_name
import kotlinx.android.synthetic.main.login_activity.ti_passwd
import me.rosuh.android.jianews.R
import me.rosuh.android.jianews.R.string
import me.rosuh.jianews.bean.User
import me.rosuh.jianews.network.ApiService
import me.rosuh.jianews.user.Configure
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
class LoginActivity : AppCompatActivity() {

    private var isLogin:AtomicBoolean = AtomicBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        btn_go_register.setOnClickListener {
            isLogin.set(isLogin.get().not())
            switchUI()
        }

        btn_login_or_register.setOnClickListener {
            if (isLogin.get()) {
                goLogin()
            } else {
                goRegister()
            }
        }
    }

    private fun switchUI() {
        if (btn_login_or_register.text == getString(R.string.go_login)) {
            btn_login_or_register.text = getString(R.string.register)
            btn_go_register.text = getString(R.string.go_login)
            register_group.visibility = View.VISIBLE
        } else {
            btn_login_or_register.text = getString(R.string.go_login)
            btn_go_register.text = getString(R.string.register)
            register_group.visibility = View.GONE
        }
    }

    private fun checkInfo(isRegister: Boolean): Boolean {
        if (ti_account.text.isNullOrBlank()) {
            Toast.makeText(this@LoginActivity, "请输入账号", Toast.LENGTH_SHORT).show()
            ti_account.requestFocus()
            return false
        }

        if (ti_passwd.text.isNullOrBlank()) {
            Toast.makeText(this@LoginActivity, "请输入密码", Toast.LENGTH_SHORT).show()
            ti_passwd.requestFocus()
            return false
        }

        if (isRegister && ti_name.text.isNullOrBlank()) {
            Toast.makeText(this@LoginActivity, "请输入昵称", Toast.LENGTH_SHORT).show()
            ti_name.requestFocus()
            return false
        }

        return true
    }

    @SuppressLint("CheckResult")
    private fun goRegister() {
        if (!checkInfo(true)){
            return
        }

        val user = User(account = ti_account.text!!.trim().toString(),
            passwd = ti_passwd.text.toString(),
            name = ti_name.text.toString()
        )
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = getString(string.registering)
        pDialog.setCancelable(true)
        ApiService.register(user)
            .doOnSubscribe {
                pDialog.show()
            }
            .subscribe(
                {
                    saveUserData(user)
                    pDialog.run {
                        titleText = "注册成功"
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    }
                    pDialog.setOnDismissListener {
                        finish()
                    }
                },
                {
                    pDialog.run {
                        titleText = "注册失败"
                        contentText = it.message
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    }
                    it.printStackTrace()
                }
            )
    }

    @SuppressLint("CheckResult")
    private fun goLogin() {
        if (!checkInfo(false)){
            return
        }
        val user = User(account = ti_account.text!!.trim().toString(), passwd = ti_passwd.text.toString())
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = getString(string.loging)
        pDialog.setCancelable(true)
        ApiService.login(user)
            .doOnSubscribe {
                pDialog.show()
            }
            .subscribe(
                {
                    saveUserData(it)
                    pDialog.run {
                        titleText = "登录成功"
                        changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    }
                    pDialog.setOnDismissListener {
                        this@LoginActivity.finish()
                    }
                },
                {
                    pDialog.run {
                        titleText = "登录失败"
                        contentText = it.message
                        changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    }
                }
            )
    }

    private fun saveUserData(user: User) {
        Configure.USERID = user.account
        if (Configure.USERID.isNotBlank()) {
            Hawk.put("USERID", Configure.USERID)
        }
        Hawk.put("user", user)
    }

    override fun onDestroy() {
        super.onDestroy()
        Configure.CALLBACK?.postExec()
    }
}