package me.rosuh.jianews.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import android.webkit.WebView
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.rosuh.android.jianews.R
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.util.Const

/**
 * 这个类是文章阅读页面的 AppCompatActivity 类
 *
 * @author rosuh 2018-5-9
 * @version 0.1
 */

class ArticleReadingFrag : BaseFragment(), CoroutineScope {

    private var mFontPopupMenu: PopupMenu? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var articleBean: ArticleBean? = null

    private lateinit var webView: WebView

    lateinit var fontItemView: View

    private val imageFixStr = "<style>img{display: inline;height: auto;max-width: 100%;}</style>"
//    private val fontsFixStr = "<style>@font-face { font-family: 'MyWebFont';src:url('file:///android_asset/fonts/SourceHanSerifCN-Regular.otf') format('opentype');font-weight: normal;font-style: normal;}</style>"
    private fun addTitleStr(title:String):String{
        return "<h2>$title</h2><hr/>"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articleBean = arguments?.getParcelable(Const.KEY_INTENT_ARTICLE_READING_ITEM) as ArticleBean
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.article_reading_frag, container, false).run {
            initWebView(this)
            webViewLoad(articleBean?.content ?: "空空如也？")
            this
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fontItemView = view.findViewById(R.id.menu_item_font)
    }

    private fun initWebView(view: View) {
        webView = view.findViewById(R.id.wv_reading)
        with(webView.settings) {
            textZoom = 125
            setSupportZoom(true)
            javaScriptEnabled = false
            builtInZoomControls = false
            loadWithOverviewMode = true
        }
    }

    override fun bindMenu(): Int = R.menu.menu_reading

    override fun initToolBar() {
        val toolbar = activity?.findViewById<Toolbar>(R.id.tb_reading) ?: return

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.inflateMenu(R.menu.menu_reading)
        toolbar.setNavigationIcon(R.drawable.ic_menu_home)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        toolbar.setNavigationOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }
        toolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                if (item == null) return false
                when (item.itemId) {
                    R.id.menu_item_font -> {
                        if (mFontPopupMenu == null) {
                            // 实例化一个 字体弹出菜单
                            mFontPopupMenu = PopupMenu(activity!!, fontItemView, Gravity.CENTER)
                            mFontPopupMenu!!.inflate(R.menu.menu_fonts)
                            mFontPopupMenu!!.setOnMenuItemClickListener {
                                when (it.itemId) {
                                    R.id.item_tiny_font -> webView.settings.textZoom = 100
                                    R.id.item_normal_font -> webView.settings.textZoom = 125
                                    R.id.item_large_font -> webView.settings.textZoom = 150
                                }
                                it.isChecked = true
                                true
                            }
                        }
                        mFontPopupMenu!!.show()
                    }
                    R.id.menu_item_link -> {
                        // 使用浏览器打开文章原始链接
                        val intentLink = Intent(Intent.ACTION_VIEW, Uri.parse(articleBean!!.url))
                        startActivity(Intent.createChooser(intentLink, "分享链接到..."))
                    }
                    R.id.menu_item_share -> {
                        // 分享按钮
                        val intentShare = Intent(Intent.ACTION_SEND)
                        intentShare.putExtra(Intent.EXTRA_TEXT, articleBean!!.title + "\n" + articleBean!!.url)
                        intentShare.type = "text/plain"
                        startActivity(
                            Intent.createChooser(
                                intentShare,
                                resources.getString(R.string.menu_item_share)
                            )
                        )
                    }
                }
                return false
            }
        })
    }

    /**
     * 监听 Fragment 消失，以便清楚 Wb 的内容
     */
    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            reset()
        }
        super.onHiddenChanged(hidden)
    }

    fun updateBean(newBean: ArticleBean) {
        this.articleBean = newBean
        webViewLoad(newBean.content)
    }

    private fun reset() = webView.loadUrl("")

    private fun webViewLoad(content: String) =
        webView.loadDataWithBaseURL(Const.URL_HOME_PAGE,
                    imageFixStr + addTitleStr(articleBean?.title?:"") + content,
            null,
            null,
            null)

    companion object {
        const val READING_FRAGMENT_TAG = "ReadingFragment"

        fun newInstance(articleBean: ArticleBean): ArticleReadingFrag {
            val frag = ArticleReadingFrag()
            frag.arguments = Bundle().apply {
                putParcelable(Const.KEY_INTENT_ARTICLE_READING_ITEM, articleBean)
            }
            return frag
        }
    }
}
