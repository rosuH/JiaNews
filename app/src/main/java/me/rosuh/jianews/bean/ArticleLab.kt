package me.rosuh.jianews.bean

import io.reactivex.schedulers.Schedulers
import me.rosuh.jianews.network.ArticleService
import me.rosuh.jianews.network.ImageService
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.Const.PageType
import me.rosuh.jianews.util.Const.PageURL.URL_CAMPUS_ACTIVITIES
import me.rosuh.jianews.util.Const.PageURL.URL_CAMPUS_ANNOUNCEMENT
import me.rosuh.jianews.util.Const.PageURL.URL_MAJOR_NEWS
import me.rosuh.jianews.util.StringUtils
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 这个类用于创建、获取 ArticleBean 及其集合的单例类
 * @author rosuh 2018-5-9
 * @version 0.1
 */

object ArticleLab {

    private val retrofit: Retrofit by lazy {
        RetrofitClient.retrofitBuilder
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
    private val articleService: ArticleService by lazy {
        retrofit.create(ArticleService::class.java)
    }

    private val imageService: ImageService by lazy {
        retrofit.create(ImageService::class.java)
    }

    /**
     * 功能：根据传入的链接，调用 WebSpider 获取数据
     * 1. 直接返回获取的文章数据
     * @param url   文章类型链接
     * @return 返回获取的文章数据
     */
    fun getArticleList(pageURL: Const.PageURL, index: Int): List<ArticleBean>? {
//        return WebSpider.getArticlesList(pageURL, index)

        return getArticleListFromServer(
            when (pageURL) {
                URL_MAJOR_NEWS -> {
                    Const.PageType.MAJOR_NEWS
                }
                URL_CAMPUS_ANNOUNCEMENT -> {
                    Const.PageType.CAMPUS_ANNOUNCEMENT
                }
                URL_CAMPUS_ACTIVITIES -> {
                    Const.PageType.CAMPUS_ACTIVITIES
                }
                else -> {
                    Const.PageType.MEDIA_REPORTS
                }
            }, indexConverter(index)
        )
    }

    private fun indexConverter(index: Int): Int {
        return index / 20
    }

    private fun getArticleListFromServer(pageType: PageType, index: Int): List<ArticleBean>? {
        return articleService
            .getArticles(pageType.toString(), index.toString())
            .subscribeOn(Schedulers.io())
            .compose(DataTransformer.getDataFromResponse(true))
            .compose(DataTransformer.getImageList(imageService))
            .toList()
            .map {
                val listArticleBean: ArrayList<ArticleBean> = ArrayList()
                for (item in it) {
                    val tmpBean = ArticleBean()
                    with(tmpBean) {
                        id = item.id
                        url = item.link
                        title = item.title
                        summary = if (item.content.isEmpty()) {
                            null
                        } else {
                            item.content.substring(0, 20)
                        }
                        thumbnail = when (item.img) {
                            1 -> item.imageList[0].link
                            else -> null
                        }
                        content = item.content
                        date = StringUtils.getFormattedTime(item.created)
                        type = item.type
                        views = item.views
                    }
                    listArticleBean.add(tmpBean)
                }
                listArticleBean
            }
            .blockingGet()
    }
}
