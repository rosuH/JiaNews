package me.rosuh.jianews.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import me.rosuh.jianews.bean.ArticleBean

/**
 *
 * @author rosuh
 * @date 2019/3/6
 */
abstract class BaseFragment : Fragment(), IView{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initToolBar()
    }

    abstract fun bindMenu(): Int

    abstract fun initToolBar()

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(bindMenu(), menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onHeaderRequestFinished(list: ArrayList<ArticleBean>) {

    }

    override fun onUpdateDataFinished(list: ArrayList<ArticleBean>, nextPos: Int) {

    }

    override fun onUpdateDataFailed(t: Throwable) {

    }

    override fun scrollToTop() {

    }
}