package me.rosuh.jianews.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.home_activity.dl_home
import kotlinx.android.synthetic.main.home_activity.nav_view
import kotlinx.android.synthetic.main.home_activity.tb_home
import me.rosuh.android.jianews.R
import me.rosuh.jianews.util.GlideApp
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import android.transition.Slide
import android.view.Gravity
import me.rosuh.android.jianews.R.color

/**
 * 首页 Activity
 * @author rosu
 */
class HomeActivity : AppCompatActivity() {

    private var isExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
//        val slideIn = TransitionInflater.from(this).inflateTransition(R.transition.slide_in)
//        val slideOut = TransitionInflater.from(this).inflateTransition(R.transition.slide_out)
//        window.apply {
//            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
//            exitTransition = slideOut
//        }
        val slide = Slide(Gravity.START)
        slide.excludeTarget(android.R.id.statusBarBackground, true)
        slide.excludeTarget(android.R.id.navigationBarBackground, true)
        window.apply {
            enterTransition = slide
            exitTransition = slide
        }
        setContentView(R.layout.home_activity)

        initToolBar()
        initNavigationView()

        var homeFragment = supportFragmentManager
            .findFragmentById(R.id.home_fragment_layout)

        if (homeFragment == null) {
            homeFragment = HomeFragment.instance
        }
        supportFragmentManager
            .beginTransaction()
            .add(R.id.content_fragment, homeFragment)
            .commit()
    }

    /**
     * 初始化顶部工具栏
     */
    private fun initToolBar() {
        setSupportActionBar(tb_home)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_home)
            setDisplayShowTitleEnabled(false)
        }
    }

    /**
     * 初始化侧滑栏
     */
    private fun initNavigationView() {
        // 侧滑栏
        nav_view.itemIconTintList = null
        val headerView = nav_view.inflateHeaderView(R.layout.nav_header)
        val imageView = headerView.findViewById<ImageView>(R.id.iv_nav_header)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.home_activity, menu)
        val item = menu.findItem(R.id.menu_item_search)
        val searchView = item.actionView as SearchView
        searchView.visibility = View.INVISIBLE
        searchView.queryHint = "请输入关键词"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //                Toast.makeText(HomeActivity.this, query, Toast.LENGTH_SHORT).show();
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> dl_home.openDrawer(GravityCompat.START)
        }
        return true
    }

    /**
     * 返回键监听
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    exitAppByDoubleClick()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

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
}
