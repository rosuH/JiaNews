package me.rosuh.jianews.storage

import me.rosuh.jianews.bean.ArticleBean

/**
 * @author rosu
 * @date 2018/9/30
 *
 * Presenter 需要实现的数据接口
 */
interface IDataModel {

    fun onInfo(info: Int)

    fun onDataResponse(articleBeanList: List<ArticleBean>)
}
