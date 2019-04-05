package me.rosuh.jianews.view

import android.view.View
import me.rosuh.jianews.bean.ArticleBean

/**
 *
 * @author rosuh
 * @date 2019/3/14
 */
interface IListClickedView {
    fun onItemClick(v: View, clickedBean:ArticleBean)
}