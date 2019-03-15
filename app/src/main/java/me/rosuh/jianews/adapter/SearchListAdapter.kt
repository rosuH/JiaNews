package me.rosuh.jianews.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.rosuh.android.jianews.R
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.view.IListClickedView

/**
 *
 * @author rosuh
 * @date 2019/3/15
 */
class SearchListAdapter(
    private val context: Activity,
    private var articleList: MutableList<ArticleBean>,
    val clickedView: IListClickedView
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return SearchListArticleHolder(LayoutInflater.from(context), parent)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is SearchListArticleHolder){
            holder.bind(articleList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return Const.VALUE_LIST_SEARCH_TYPE
    }

    fun updateData(articleList:ArrayList<ArticleBean>){
        this.articleList = articleList
    }

    /**
     * 文章类 Holder
     */
    inner class SearchListArticleHolder constructor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.list_item_article_search, parent, false
            )
        ), View.OnClickListener {

        var mArticleBean: ArticleBean? = null

        private val mTitleTextView: TextView = itemView.findViewById(R.id.tv_article_title)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(articleBean: ArticleBean) {
            this.mArticleBean = articleBean
            mTitleTextView.text = mArticleBean!!.title
        }

        override fun onClick(v: View) {
            // 实现点击启动特定 article 的阅读页面
            if (mArticleBean!!.content.isEmpty() && mArticleBean!!.thumbnail.isEmpty()) {
                // 媒体报道直接打开浏览器
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(mArticleBean!!.url)
                context.startActivity(intent)
            } else {
                clickedView.onItemClick(mArticleBean!!)
            }
        }
    }
}