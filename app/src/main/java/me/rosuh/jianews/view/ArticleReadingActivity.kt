package me.rosuh.jianews.view

import android.text.Html.FROM_HTML_MODE_LEGACY

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.view.Window
import android.widget.TextView
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.rosuh.android.jianews.R
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.TextViewImageGetter

/**
 * 这个类是文章阅读页面的 AppCompatActivity 类
 *
 * @author rosuh 2018-5-9
 * @version 0.1
 */

class ArticleReadingActivity : AppCompatActivity(), CoroutineScope, PopupMenu.OnMenuItemClickListener,
    PopupMenu.OnDismissListener {

    private var mFontPopupMenu: PopupMenu? = null

    private var tvContent: TextView? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var mArticleBean: ArticleBean? = null

    private var fontItemView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        window.apply {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = null
        }
        setContentView(R.layout.article_reading_frag)
        mArticleBean = intent.getParcelableExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM)

        val mToolbar = findViewById<Toolbar>(R.id.tb_reading)
        tvContent = findViewById(R.id.tv_article_content)
        setSupportActionBar(mToolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_back)
        }
        tvContent!!.text = produceContent(mArticleBean)
        tvContent!!.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun produceContent(articleBean: ArticleBean?): Spanned? {
        if (articleBean == null) {
            return null
        }

        var tmpStr = articleBean.content
        for (link in articleBean.imagesList) {
            tmpStr = "$tmpStr<img src=\"$link\" width=\"600\"/><br/><br/>"
        }
        articleBean.content = tmpStr

        val contentSpanned: Spannable = if (VERSION.SDK_INT >= VERSION_CODES.N) {
            Html.fromHtml(
                "<h1>" + mArticleBean!!.title + "</h1><br/>" +
                        "<i>" + mArticleBean!!.date + "</><br/>" +
                        mArticleBean!!.content,
                FROM_HTML_MODE_LEGACY,
                TextViewImageGetter(this, this, tvContent!!), null
            ) as Spannable
        } else {
            Html.fromHtml(
                "<h1>" + mArticleBean!!.title + "</h1><br/>" +
                        "<i>" + mArticleBean!!.date + "</><br/>" +
                        mArticleBean!!.content,
                TextViewImageGetter(this, this, tvContent!!), null
            ) as Spannable
        }

        for (span in contentSpanned.getSpans(0, contentSpanned.length, ImageSpan::class.java)) {
            val flags = contentSpanned.getSpanFlags(span)
            val start = contentSpanned.getSpanStart(span)
            val end = contentSpanned.getSpanEnd(span)

            contentSpanned.setSpan(object : URLSpan(span.source) {
                override fun onClick(widget: View) {
                    super.onClick(widget)
                    Log.i("Image Clicked", "onClick: ===================" + widget.toString())
                }
            }, start, end, flags)
        }

        return contentSpanned
    }

    private fun produceImageView(context: Context, imgList: List<String>){
//        for ()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.article_reading_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (fontItemView == null) {
            fontItemView = this@ArticleReadingActivity.findViewById(R.id.menu_item_font)
        }
        when (item.itemId) {
            R.id.menu_item_font -> {
                if (fontItemView == null) {
                    return false
                }
                if (mFontPopupMenu == null) {
                    mFontPopupMenu = PopupMenu(this@ArticleReadingActivity, fontItemView!!, Gravity.CENTER)
                    mFontPopupMenu!!.inflate(R.menu.reading_font_menu)
                    mFontPopupMenu!!.setOnMenuItemClickListener(this)
                    mFontPopupMenu!!.setOnDismissListener(this)
                }
                mFontPopupMenu!!.show()
            }
            R.id.menu_item_link -> {
                // 使用浏览器打开文章原始链接
                val intentLink = Intent(Intent.ACTION_VIEW, Uri.parse(mArticleBean!!.url))
                startActivity(Intent.createChooser(intentLink, "分享链接到..."))
            }
            R.id.menu_item_share -> {
                // 分享按钮
                val intentShare = Intent(Intent.ACTION_SEND)
                intentShare.putExtra(Intent.EXTRA_TEXT, mArticleBean!!.title + "\n" + mArticleBean!!.url)
                intentShare.type = "text/plain"
                startActivity(Intent.createChooser(intentShare, resources.getString(R.string.menu_item_share)))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        if (tvContent == null) {
            return false
        }
        when (menuItem.itemId) {
            R.id.item_tiny_font -> tvContent!!.textSize = 15f
            R.id.item_normal_font -> tvContent!!.textSize = 18f
            R.id.item_large_font -> tvContent!!.textSize = 20f
        }
        menuItem.isChecked = true
        return true
    }

    override fun onDismiss(popupMenu: PopupMenu) {
    }

    companion object {

        fun newIntent(articleBean: ArticleBean, activity: Activity): Intent {
            val intent = Intent(activity, ArticleReadingActivity::class.java)
            intent.putExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM, articleBean)
            return intent
        }
    }
}
