package me.rosuh.jianews.view

import me.rosuh.jianews.bean.ArticleBean

/**
 *
 * @author rosuh
 * @date 2019/3/14
 */
interface IListClickedView {
    fun onItemClick(clickedBean:ArticleBean)
}