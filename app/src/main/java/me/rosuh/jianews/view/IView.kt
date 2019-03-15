package me.rosuh.jianews.view

import me.rosuh.jianews.bean.ArticleBean

/**
 * @author rosu
 * @date 2018/9/30
 */
interface IView {

    fun onHeaderRequestFinished(list: ArrayList<ArticleBean>)

    fun onUpdateDataFinished(list: ArrayList<ArticleBean>, nextPos: Int)

    fun onUpdateDataFailed(t: Throwable)

    fun scrollToTop()
}
