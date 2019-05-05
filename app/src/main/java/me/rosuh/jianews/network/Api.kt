package me.rosuh.jianews.network

import io.reactivex.Observable
import me.rosuh.jianews.bean.ArticleDataItem
import me.rosuh.jianews.bean.BoardBean
import me.rosuh.jianews.bean.DataBean
import me.rosuh.jianews.bean.User
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
interface Api {

    @GET("api/v1/articles/get")
    fun getArticles(@Query("type") articleType: String, @Query("offset") offset: String): Observable<DataBean<List<ArticleDataItem>>>

    @GET("api/v1/articles/search")
    fun searchArticles(@Query("word") keyWord: String): Observable<DataBean<List<ArticleDataItem>>>

    @FormUrlEncoded
    @POST("/api/v1/user/login")
    fun login(@Field("account") account: String, @Field("passwd") passwd: String): Observable<DataBean<User>>

    @FormUrlEncoded
    @POST("/api/v1/user/logout")
    fun logout(@Field("account") account: String, @Field("passwd") passwd: String): Observable<DataBean<Any>>

    @FormUrlEncoded
    @POST("/api/v1/user/register")
    fun register(
        @Field("account") account: String,
        @Field("passwd") passwd: String,
        @Field("name") name: String,
        @Field("description") description: String
    ): Observable<DataBean<Any>>

    @GET("/api/v1/board/get")
    fun getBoards(@Query("offset") offset: String): Observable<DataBean<List<BoardBean>>>

    @FormUrlEncoded
    @POST("/api/v1/board/add")
    fun addBoard(
        @Field("author") author: String,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("board_tag") board_tag: String
    ): Observable<DataBean<BoardBean>>
}