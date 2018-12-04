package me.rosuh.jianews.bean

import android.text.TextUtils

import me.rosuh.jianews.network.WebSpider
import me.rosuh.jianews.util.Const

/**
 * 这个类用于创建、获取 ArticleBean 及其集合的单例类
 * @author rosuh 2018-5-9
 * @version 0.1
 */

object ArticleLab {

    /**
     * 功能：根据传入的链接，调用 WebSpider 获取数据
     * 1. 直接返回获取的文章数据
     * @param url   文章类型链接
     * @return 返回获取的文章数据
     */
    fun getArticleList(pageURL: Const.PageURL, index: Int): List<ArticleBean>? {
        return WebSpider.getArticlesList(pageURL, index)
    }
}
