package me.rosuh.jianews.network


import io.reactivex.Flowable
import io.reactivex.Observable
import me.rosuh.jianews.bean.ArticleDataItem
import me.rosuh.jianews.bean.DataBean
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
interface ArticleService{
    @GET("api/v1/articles/get")
    fun getArticles(@Query("type") articleType:String, @Query("offset") offset:String): Observable<DataBean<List<ArticleDataItem>>>
    fun searchArticles(@Query("word") keyWord:String): Observable<DataBean<List<ArticleDataItem>>>
}