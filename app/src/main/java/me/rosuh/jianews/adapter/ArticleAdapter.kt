package me.rosuh.jianews.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.constraint.Guideline
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import me.rosuh.android.jianews.R
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.GlideApp
import me.rosuh.jianews.util.MyGlideExtension
import me.rosuh.jianews.view.ArticleReadingActivity
import java.util.ArrayList

/**
 *
 * @author rosuh
 * @date 2018/12/21
 */
class ArticleAdapter(private val context: Activity, articleBeans: MutableList<ArticleBean>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mArticleBeans: MutableList<ArticleBean>? = null

    private var mArticleBean: ArticleBean? = null

    private var mFooterHolder: FooterHolder? = null

    lateinit var itemClickListener: View.OnClickListener

    init {
        mArticleBeans = articleBeans
        if (mArticleBeans == null) {
            mArticleBeans = ArrayList()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        when (viewType) {
            Const.VALUE_LIST_EMPTY_TYPE -> return EmptyHolder(layoutInflater, parent)
            Const.VALUE_LIST_FOO_TYPE -> return mFooterHolder!!
            else -> return ArticleHolder(layoutInflater, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleHolder) {
            mArticleBean = mArticleBeans!![position]
            holder.bind(context, mArticleBean!!)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mArticleBeans!!.size != 0 && position >= mArticleBeans!!.size) {
            // 返回 footer 布局
            return Const.VALUE_LIST_FOO_TYPE
        }
        return if (mArticleBeans!!.size == 0) {
            // 返回空视图
            Const.VALUE_LIST_EMPTY_TYPE
        } else Const.VALUE_LIST_DEFAULT_TYPE
    }

    override fun getItemCount(): Int {
        return if (mArticleBeans.isNullOrEmpty()) {
            Const.VALUE_LIST_DEFAULT_SIZE
        } else mArticleBeans!!.size + 1
    }

    fun addItems(articleBeans: List<ArticleBean>) {
        mArticleBeans!!.addAll(articleBeans)
    }

    fun setFooterHolder(footerHolder: FooterHolder?) {
        mFooterHolder = footerHolder
    }

    /**
     * 文章类 Holder
     */
    inner class ArticleHolder constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.list_item_article, parent, false
            )
        ), View.OnClickListener {

        var mArticleBean: ArticleBean? = null

        private val mTitleTextView: TextView = itemView.findViewById(R.id.tv_article_title)

        private val mPublishTimeTextView: TextView = itemView.findViewById(R.id.tv_list_publish_time)

        private val mThumbnailImageView: ImageView = itemView.findViewById(R.id.iv_article_thumbnail)

        private val tvViews:TextView = itemView.findViewById(R.id.tv_views)

        init {
            itemView.setOnClickListener(this)
        }

        /**
         * 功能：被 Adapter 调用来绑定数据和视图
         * 1. 由 adapter 传入一个 articleBean 对象
         * 2. 由本方法绑定
         *
         * @param articleBean 传入已填充数据的 articleBean 对象
         */
        fun bind(context: Context, articleBean: ArticleBean) {
            this.mArticleBean = articleBean
            mTitleTextView.text = mArticleBean!!.title
            if (mArticleBean!!.thumbnail == null){
                itemView.findViewById<ImageView>(R.id.iv_article_thumbnail).visibility = View.GONE
            }else {
                itemView.findViewById<ImageView>(R.id.iv_article_thumbnail).visibility = View.VISIBLE
            }
            GlideApp.with(context)
                .load(mArticleBean!!.thumbnail)
                .apply(MyGlideExtension.getOptions(RequestOptions(), context, 3))
                .into(mThumbnailImageView)
            mPublishTimeTextView.text = mArticleBean!!.date
            tvViews.text = mArticleBean!!.views.toString()
            if (articleBean.type == Const.PageType.MEDIA_REPORTS.toString()){
                tvViews.visibility = View.INVISIBLE
                itemView.findViewById<ImageView>(R.id.iv_views).visibility = View.INVISIBLE
            }
        }

        override fun onClick(v: View) {
            if (mArticleBean!!.content == null && mArticleBean!!.thumbnail == null) {
                // 实现点击启动特定 article 的阅读页面
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(mArticleBean!!.url)
                context.startActivity(intent)
            } else {
                context.startActivity(ArticleReadingActivity.newIntent(mArticleBean, context))
            }
        }
    }

    /**
     * @author rosuh 2018-4-26 21:00:34
     * 功能：空视图 Holder 类，在真正的数据还没有获得前，加载本 holder
     */
    inner class EmptyHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_empty, parent, false))

    /**
     * @author rosuh 2018-4-28 21:43:21
     * 功能：上拉加载 Holder 类。当用户上拉到底的时候，加载本 holder
     */
    class FooterHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_footer, parent, false)) {

        private val mTvTips: TextView

        private val mProgressBar: ContentLoadingProgressBar

        init {
            val view = inflater.inflate(R.layout.list_item_footer, parent, false)
            mTvTips = view.findViewById(R.id.tv_item_footer)
            mProgressBar = view.findViewById(R.id.pb_item_footer)
        }
    }
}