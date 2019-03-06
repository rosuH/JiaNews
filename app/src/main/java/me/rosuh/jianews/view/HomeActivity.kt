package me.rosuh.jianews.view

import android.graphics.Color
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.transition.Fade
import android.support.transition.TransitionInflater
import android.support.transition.TransitionSet
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.article_reading_frag.tb_reading
import kotlinx.android.synthetic.main.home_activity.dl_home
import kotlinx.android.synthetic.main.home_activity.nav_view
import kotlinx.android.synthetic.main.home_fragment.tb_home
import me.rosuh.android.jianews.R
import me.rosuh.jianews.util.GlideApp
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.util.DrawerLocker
import me.rosuh.jianews.view.ArticleReadingFrag.Companion.READING_FRAGMENT_TAG

/**
 * 首页 Activity
 * @author rosu
 */
class HomeActivity : AppCompatActivity(), DrawerLocker{

    private var isExit = false

    private lateinit var readingFrag: ArticleReadingFrag

    lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        initNavigationView()
        addHomeFragment()
        tryPreLoadWebView()
    }

    /**
     * 初始化侧滑栏
     */
    private fun initNavigationView() {
        val headerView = nav_view.inflateHeaderView(R.layout.nav_header)
        val imageView = headerView.findViewById<ImageView>(R.id.iv_nav_header)
        nav_view.itemIconTintList = null
        imageView.setOnClickListener {
            val iv = ImageView(this@HomeActivity)
            iv.setBackgroundColor(Color.parseColor("#99000000"))
            GlideApp.with(this)
                .load(R.drawable.easter_egg)
                .into(iv)
            if (iv.parent != null) {
                (iv.parent as ViewGroup).removeView(iv)
            }
            iv.animate().alpha(1f).setDuration(1000).start()
            iv.setOnClickListener {
                iv.imageAlpha = 0
                if (iv.parent != null) {
                    (iv.parent as ViewGroup).removeView(iv)
                }
            }
            this@HomeActivity.addContentView(
                iv,
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            )
        }
        nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_item_about -> AboutPageDialog().show(supportFragmentManager, "About Dialog")
            }
            dl_home.closeDrawers()
            false
        }
    }

    /**
     * 添加主视图
     */
    private fun addHomeFragment() {
        homeFragment = supportFragmentManager
            .findFragmentById(R.id.home_fragment_layout) as? HomeFragment ?: HomeFragment.instance

        supportFragmentManager
            .beginTransaction()
            .add(R.id.content_fragment, homeFragment, HomeFragment.HOME_FRAGMENT_TAG)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                dl_home.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 双击退出实现
     */
    private fun exitAppByDoubleClick() {
        val scheduledExecutorService = Executors.newScheduledThreadPool(1)
        if (isExit) {
            finish()
        } else {
            isExit = true
            Toast.makeText(this, "再点击一次退出", Toast.LENGTH_LONG)
                .show()
            scheduledExecutorService.schedule({ isExit = false }, 2000, TimeUnit.MILLISECONDS)
        }
    }

    /**
     * 列表条目被点击，调用此方法来控制阅读视图和主视图的显隐性
     */
    fun onItemClick(clickedBean: ArticleBean) {
        if (isFragmentExist(ArticleReadingFrag.READING_FRAGMENT_TAG)) {
            readingFrag = supportFragmentManager.findFragmentByTag(READING_FRAGMENT_TAG) as ArticleReadingFrag
            readingFrag.updateBean(clickedBean)
        } else {
            readingFrag = ArticleReadingFrag.newInstance(clickedBean)
        }

        supportFragmentManager.beginTransaction().apply {
            if (!readingFrag.isAdded) {
                add(R.id.content_fragment, readingFrag, ArticleReadingFrag.READING_FRAGMENT_TAG)
            }
            setCustomAnimations(
                R.anim.push_right_in, R.anim.push_left_out
            )
            hide(homeFragment)
            show(readingFrag)
            commit()
        }
    }

    override fun setDrawerLocked(shouldLock: Boolean) {
        if (shouldLock){
            dl_home.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }else{
            dl_home.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    /**
     * 判断 Fragment 是否已经实例化
     */
    private fun isFragmentExist(tag: String): Boolean =
        supportFragmentManager.findFragmentByTag(tag) != null

    /**
     * 预热WebView
     */
    private fun tryPreLoadWebView(){
        WebView(this).destroy()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(ArticleReadingFrag.READING_FRAGMENT_TAG)?.isHidden == true) {
            // 如果在首页 Fragment
            exitAppByDoubleClick()
        } else {
            // 如果在阅读视图
            supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.push_left_in, R.anim.push_right_out
                )
                show(homeFragment)
                hide(readingFrag)
                commit()
            }
        }
    }
}
