package me.rosuh.jianews.precenter

import java.util.concurrent.ConcurrentHashMap

import io.reactivex.android.schedulers.AndroidSchedulers
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.bean.ArticleLab
import me.rosuh.jianews.storage.IDataModel
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.StringUtils
import me.rosuh.jianews.util.ViewUtils
import me.rosuh.jianews.view.ArticleListFragment
import me.rosuh.jianews.view.IView

/**
 * @author rosu
 * @date 2018/9/30
 */
object ArticleListViewPresenter: IDataModel {
    private val mIViewMap = ConcurrentHashMap<Const.PageURL, IView>()

    /**
     * 获取数据
     * @param context
     * @param index 数据页索引
     * @param url   数据页链接
     */
    fun requestHeaderData(context: ArticleListFragment?, index: Int, pageURL: Const.PageURL) {
        context?:return

        mIViewMap[pageURL] = context
        // 获取到数据则通知列表更新
        if (index != Const.VALUE_ARTICLE_INDEX_START) {
            return
        }
        val list = ArticleLab.getArticleList(Const.PageURL.URL_MAJOR_NEWS, index)
        if (ViewUtils.isInMainThread()) {
            mIViewMap[pageURL]!!.onStartRequest(list)
        } else {
            AndroidSchedulers.mainThread().scheduleDirect { mIViewMap[pageURL]!!.onStartRequest(list) }
        }

    }


    fun requestMoreData(context: ArticleListFragment?, index: Int, pageURL: Const.PageURL) {
        if (context == null) {
            return
        }
        mIViewMap[pageURL] = context
        // 获取到数据则通知列表更新
        if (index == Const.VALUE_ARTICLE_INDEX_START) {
            return
        }

        val list = ArticleLab.getArticleList(pageURL, index)
        if (ViewUtils.isInMainThread()) {
            mIViewMap[pageURL]?.onUpdateDataFinished(list, index + 1)
        } else {
            AndroidSchedulers.mainThread().scheduleDirect { mIViewMap[pageURL]?.onUpdateDataFinished(list, index + 1) }
        }
    }

    override fun onInfo(info: Int) {

    }

    override fun onDataResponse(articleBeanList: List<ArticleBean>) {

    }
}