package me.rosuh.jianews.network

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import me.rosuh.jianews.bean.BoardBean
import me.rosuh.jianews.bean.DataTransformer
import me.rosuh.jianews.bean.User
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
object ApiService {

    private val retrofit: Retrofit by lazy {
        RetrofitClient
            .retrofitBuilder
            .client(HttpClient.builder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private val API: Api by lazy {
        retrofit.create(Api::class.java)
    }

    fun register(user: User):Observable<Any>{
        return API
            .register(account = user.account, passwd = user.passwd, name = user.name, description = user.description)
            .compose(DataTransformer.getDataFromResponse(true))
            .compose(DataTransformer.applySchedulers())
    }

    fun login(user: User):Observable<User>{
        return API.login(account = user.account, passwd = user.passwd)
            .compose(DataTransformer.getDataFromResponse(true))
            .compose(DataTransformer.applySchedulers())

    }

    fun logout(user: User):Observable<Any>{
        return API.logout(account = user.account, passwd = user.passwd)
            .compose(DataTransformer.getDataFromResponse(true))
            .compose(DataTransformer.applySchedulers())

    }

    data class BoardEntity(var title: String, var content: String, var board_tag: String, var author: String)

    fun getBoards(offset: Int): Observable<List<BoardBean>> {
        return API.getBoards(offset = offset.toString())
            .subscribeOn(Schedulers.io())
            .compose(DataTransformer.getDataFromResponse(true))
            .compose(DataTransformer.applySchedulers())
    }

    fun addBoard(boardEntity: BoardEntity): Observable<BoardBean> {
        return API.addBoard(
            boardEntity.author, boardEntity.title, boardEntity.content, boardEntity.board_tag
        )
            .subscribeOn(Schedulers.io())
            .compose(DataTransformer.getDataFromResponse(true))
            .compose(DataTransformer.applySchedulers())
    }
}