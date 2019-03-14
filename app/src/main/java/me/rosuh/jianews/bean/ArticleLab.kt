package me.rosuh.jianews.bean

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import me.rosuh.jianews.network.ArticleService
import me.rosuh.jianews.network.ImageService
import me.rosuh.jianews.network.RetrofitClient
import me.rosuh.jianews.util.Const
import me.rosuh.jianews.util.Const.PageType
import me.rosuh.jianews.util.Const.PageURL.URL_CAMPUS_ANNOUNCEMENT
import me.rosuh.jianews.util.Const.PageURL.URL_MAJOR_NEWS
import me.rosuh.jianews.util.Const.VALUE_LIST_DEFAULT_SIZE
import me.rosuh.jianews.util.StringUtils
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException

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
    fun getArticleList(pageURL: Const.PageURL, index: Int): Observable<ArrayList<ArticleBean>> {
//        return WebSpider.getArticlesList(pageURL, index)

        return getArticleListFromServer(
            when (pageURL) {
                URL_MAJOR_NEWS -> {
                    Const.PageType.MAJOR_NEWS
                }
                URL_CAMPUS_ANNOUNCEMENT -> {
                    Const.PageType.CAMPUS_ANNOUNCEMENT
                }
                else -> {
                    Const.PageType.MEDIA_REPORTS
                }
            }, indexConverter(index)
        )
    }

    private fun indexConverter(index: Int): Int {
        return index / VALUE_LIST_DEFAULT_SIZE
    }

    private fun getArticleListFromServer(pageType: PageType, index: Int): Observable<ArrayList<ArticleBean>> {
        return articleService
            .getArticles(pageType.toString(), index.toString())
            .subscribeOn(Schedulers.io())
            .onErrorReturn { t: Throwable ->
                if (t is ConnectException || t is UnknownHostException){
                    DataBean(code = 0, msg = "糟糕！网络好像不太稳定！(╬ﾟ ◣ ﾟ)", data = emptyList())
                }else {
                    val errorCode = (t as HttpException).code()
                    when(errorCode) {
                        in 500..599 -> {
                            DataBean(code = errorCode, msg = "服务器发生了一点小错误(,,・ω・,,)", data = emptyList())
                        }
                        in 400..499 -> {
                            DataBean(code = errorCode, msg = "发送的请求有误 (‘⊙д-)", data = emptyList())
                        }
                        else -> {
                            DataBean(code = errorCode, msg = "我也不知道发生了啥错误...", data = emptyList())
                        }
                    }
                }
            }
            .compose(DataTransformer.getDataFromResponse(true))
            .compose(DataTransformer.getImageList(imageService))
            .toList()
            .map {
                val listArticleBean: ArrayList<ArticleBean> = ArrayList()
                val isMediaResports = pageType == PageType.MEDIA_REPORTS
                for (item in it) {
                    listArticleBean.add(
                        ArticleBean(
                            id = item.id,
                            url = item.link,
                            title = item.title,
                            summary = if (isMediaResports || item.content.isEmpty() || item.content.length <= 20) {
                                ""
                            } else {
                                item.content.substring(0, 20)
                            },
                            imagesList = if (item.img == 1){
                                item.imageList.map { imageDataItem -> imageDataItem.link }
                            }else {
                                emptyList()
                            },
                            thumbnail = if (item.imageList.isNullOrEmpty()) {
                                ""
                            } else {
                                item.imageList[0].link
                            },
                            isRead = false,
                            content = if (item.content.isNullOrBlank()){
                                ""
                            }else {
                                item.content
                            },
                            date = StringUtils.getFormattedTime(item.created),
                            type = item.type,
                            views = item.views
                        )
                    )
                }
                listArticleBean
            }
            .toObservable()
    }
}
