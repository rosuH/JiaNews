package me.rosuh.jianews.precenter

import io.reactivex.Scheduler
import java.util.concurrent.ConcurrentHashMap

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.rosuh.jianews.bean.ArticleBean
import me.rosuh.jianews.bean.ArticleLab
import me.rosuh.jianews.storage.IDataModel
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.Const.PageURL
import me.rosuh.jianews.view.ArticleListFragment
import me.rosuh.jianews.view.IView

/**
 * @author rosu
 * @date 2018/9/30
 */
object ArticleListViewPresenter : IDataModel {

    private val mIViewMap = ConcurrentHashMap<Const.PageURL, IView>()
    private var disposableMap = ConcurrentHashMap<Const.PageURL, Disposable>()

    /**
     * 获取数据
     * @param context
     * @param index 数据页索引
     * @param pageURL   数据页链接
     */
    fun requestHeaderData(context: ArticleListFragment?, index: Int, pageURL: Const.PageURL) {
        context ?: return

        mIViewMap[pageURL] = context
        // 获取到数据则通知列表更新
        if (index != Const.VALUE_ARTICLE_INDEX_START) {
            return
        }
        ArticleLab
            .getArticleList(pageURL, index)
            .doOnSubscribe { disposable ->
                register(pageURL, disposable)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mIViewMap[pageURL]!!.onHeaderRequestFinished(it)
                },
                {
                    mIViewMap[pageURL]?.onUpdateDataFailed(it)
                    disposableMap[pageURL]?.dispose()
                }
            ).isDisposed
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
        ArticleLab
            .getArticleList(pageURL, index)
            .doOnSubscribe { disposable ->
                register(pageURL, disposable)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mIViewMap[pageURL]?.onUpdateDataFinished(it, index)
                },
                {
                    mIViewMap[pageURL]!!.onUpdateDataFailed(it)
                    disposableMap[pageURL]?.dispose()
                }
            ).isDisposed
    }

    fun searchData(iView: IView, keyWord:String){
        val disposable = ArticleLab
            .searchArticles(keyWord)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    iView.onHeaderRequestFinished(it)
                },
                {
                    iView.onUpdateDataFailed(it)
                }
            )
    }

    private fun register(pageURL: PageURL, disposable: Disposable) {
        disposableMap[pageURL] = disposable
    }

    fun disposeAll() {
        for (disposable in disposableMap.values) {
            disposable.dispose()
        }
        mIViewMap.clear()
    }

    override fun onInfo(info: Int) {
    }

    override fun onDataResponse(articleBeanList: List<ArticleBean>) {
    }
}
