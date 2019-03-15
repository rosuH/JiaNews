package me.rosuh.jianews.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import java.util.ArrayList

import java.util.Objects
import java.util.concurrent.atomic.AtomicBoolean
import me.rosuh.android.jianews.R
import me.rosuh.jianews.adapter.ArticleAdapter
import me.rosuh.jianews.adapter.ArticleAdapter.FooterHolder
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.precenter.ArticleListViewPresenter
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.ResponseThrowable

import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE

/**
 * 这个类是文章列表的 Fragment 类，在这里类中实现了：
 * 1. 对 RecyclerView 的初始化
 * - 当数据没有准备好时，加载 Empty 视图
 * - 当数据准备好时，刷新列表视图
 * 2. 下拉刷新
 * 3. 上拉加载视图
 * 4. 通过 item 的点击来启动对应的文章阅读页面
 *
 * @author rosuh 2018-5-9
 * @version 0.1l
 */
class ArticleListFragment : Fragment(), IView, IListClickedView {

    private var mRVArticles: RecyclerView? = null

    private var mArticleAdapter: ArticleAdapter? = null

    private var mArticleBeans: MutableList<ArticleBean> = ArrayList(Const.VALUE_LIST_DEFAULT_SIZE)

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    private var mContext: Context? = null

    private var mRequestUrl: Const.PageURL = Const.PageURL.URL_MAJOR_NEWS

    private var mToast: Toast? = null

    private var mLinearLayoutManager: LinearLayoutManager? = null

    private val mViewPresenter = ArticleListViewPresenter

    private val mIsRequesting = AtomicBoolean(false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 把 Viewpager 的 position 转换为相对应的文章类型页面链接
        if (arguments != null) {
            mRequestUrl = arguments!!.getSerializable(Const.KEY_ARGS_ARTICLES_PAGE_URL) as Const.PageURL
        }
        // 执行数据获取工作
        loadHeaderData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.article_list_page_fragment, container, false)
        mSwipeRefreshLayout = view.findViewById(R.id.srl_list)
        mRVArticles = view.findViewById(R.id.rv_article_list)
        initRv()
        // 下拉刷新布局
        mSwipeRefreshLayout!!.setColorSchemeColors(ContextCompat.getColor(mContext!!, R.color.colorAccent))
        mSwipeRefreshLayout!!.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { this.loadHeaderData() })
        mSwipeRefreshLayout!!.isRefreshing = true
        updateUI()
        return view
    }

    private fun initRv() {
        mLinearLayoutManager = LinearLayoutManager(activity)
        mRVArticles!!.layoutManager = mLinearLayoutManager

        val itemAnimator = DefaultItemAnimator()
        itemAnimator.apply {
            addDuration = 1400
            changeDuration = 1400
            moveDuration = 1400
            removeDuration = 1400
        }
        mRVArticles!!.itemAnimator = itemAnimator

        mRVArticles!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastVisibleItemPos = mLinearLayoutManager!!.findLastVisibleItemPosition()
                val lastType = mArticleAdapter!!.getItemViewType(lastVisibleItemPos)
                // 如果最后一个 item 的类型是 VALUE_LIST_FOO_TYPE，那么调用加载更多数据方法
                when (newState) {
                    SCROLL_STATE_IDLE -> if (lastType == Const.VALUE_LIST_FOO_TYPE) {
                        loadMoreData(lastVisibleItemPos)
                    }
                }
            }
        })
    }

    /**
     * 异步获取数据
     */
    private fun loadHeaderData() {
        if (mIsRequesting.compareAndSet(false, true)) {
            mViewPresenter.requestHeaderData(
                this@ArticleListFragment,
                Const.VALUE_ARTICLE_INDEX_START, mRequestUrl
            )
        }
    }

    /**
     * @param position 传入 mLinearLayoutManager.findLastCompletelyVisibleItemPosition() 作为当前可是列表最后项
     */
    private fun loadMoreData(position: Int) {
        Log.i(TAG, "loadMoreData: position ==== $position")
        if (mIsRequesting.compareAndSet(false, true)) {
            mViewPresenter
                .requestMoreData(this@ArticleListFragment, position, mRequestUrl)
        }
    }

    /**
     * 功能：根据数据创建适配器，将之设置给列表，后更新 UI
     */
    private fun updateUI() {
        mArticleAdapter = ArticleAdapter(Objects.requireNonNull<FragmentActivity>(this.activity), mArticleBeans, this)
        if (mRVArticles == null) return
        mRVArticles!!.adapter = mArticleAdapter
        mArticleAdapter!!.setFooterHolder(
            FooterHolder(Objects.requireNonNull<FragmentActivity>(activity).getLayoutInflater(), mRVArticles!!)
        )
        mArticleAdapter!!.notifyItemInserted(mArticleBeans.size)
        stopSwipeRefresh()
    }

    private fun stopSwipeRefresh() {
        if (mSwipeRefreshLayout!!.isRefreshing && !mArticleBeans.isEmpty()) {
            mSwipeRefreshLayout!!.isRefreshing = false
        }
    }

    override fun onHeaderRequestFinished(list: ArrayList<ArticleBean>) {
        mIsRequesting.compareAndSet(true, false)
        stopRefresh(list)
    }

    override fun onUpdateDataFinished(list: ArrayList<ArticleBean>, nextPos: Int) {
        mIsRequesting.compareAndSet(true, false)
        stopLoading(list)
    }

    override fun onUpdateDataFailed(t: Throwable) {
        mIsRequesting.compareAndSet(true, false)
        t.printStackTrace()
        Toast.makeText(mContext, t.message + "\n 请稍后重试", Toast.LENGTH_LONG).show()
        if (t.javaClass == ResponseThrowable::class.java) {
            mViewPresenter.disposeAll()
        }
        mSwipeRefreshLayout!!.isRefreshing = false
        mSwipeRefreshLayout!!.visibility = View.INVISIBLE
        mSwipeRefreshLayout!!.visibility = View.VISIBLE
    }

    override fun scrollToTop() {
        mRVArticles!!.smoothScrollToPosition(0)
    }

    /**
     * 功能：此方法在更新网络请求完成后被调用
     * 1. 暂停下拉刷新动画
     * 2. 通知列表数据改变
     */
    private fun stopRefresh(list: MutableList<ArticleBean>) {
        if (isListEmpty(list)) {
            return
        }
        if (isListEmpty(mArticleBeans)) {
            // 如果为空，则直接赋值
            mArticleBeans = list
        } else if (isNewData(mArticleBeans, list)) {
            // 先获取增加的数据，然后把新数据复制到 mArticleBeans 头部
            val index = list.indexOf(mArticleBeans[0])
            val tmpList = list.subList(0, index)
            mArticleBeans.addAll(0, tmpList)
        }
        //        showToast(R.string.item_refresh_finished);
        updateUI()
    }

    private fun stopLoading(list: List<ArticleBean>) {
        val prePos = mArticleBeans.size
        if (isListEmpty(list)) {
            // 没有更多数据
            showToast("没有更多文章啦~")
            mArticleAdapter!!.setFooterHolder(null)
            mArticleAdapter!!.notifyItemRemoved(mArticleAdapter!!.itemCount + 1)
            return
        }
        mArticleAdapter!!.addItems(list)
        mArticleAdapter!!.notifyItemRangeInserted(prePos + 1, mArticleBeans.size)
    }

    /**
     * 功能：比较两个列表是否一致，以确认是否有新数据
     *
     * @param ori 原始列表
     * @param des 新的列表
     * @return 如果有新数据，返回 true，没有则返回 false
     */
    private fun isNewData(ori: List<ArticleBean>, des: List<ArticleBean>): Boolean {
        return ori[0].id != des[0].id
    }

    /**
     * 功能：若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     *
     * @return 若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     */
    private fun isListEmpty(list: List<*>?): Boolean {
        return list == null || list.isEmpty()
    }

    override fun onItemClick(clickedBean: ArticleBean) {
        (activity as HomeActivity).onItemClick(clickedBean)
    }

    /**
     * 显示 Toast
     *
     * @param resId 资源 id
     */
    private fun showToast(resId: Int) {
        showToast(getString(resId))
    }

    private fun showToast(str: String) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(str)
            mToast!!.duration = Toast.LENGTH_SHORT
        }
        mToast!!.show()
    }

    /**
     * 取消 Toast
     */
    private fun cancelToast() {
        if (mToast == null) {
            return
        }
        mToast!!.cancel()
    }

    companion object {

        private val TAG = "ArticleListFragment"

        /**
         * @param pageURL TabLayout 选中的 item 位置
         * @return 附带有 position 的 fragment 实例
         */
        fun getInstances(pageURL: Const.PageURL): Fragment {
            val args = Bundle()
            args.putSerializable(Const.KEY_ARGS_ARTICLES_PAGE_URL, pageURL)
            val fragment = ArticleListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
