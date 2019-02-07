package me.rosuh.jianews.network


import io.reactivex.Flowable
import io.reactivex.Observable
import me.rosuh.jianews.bean.DataBean
import me.rosuh.jianews.bean.ImageDataItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
interface ImageService {
    @GET("api/v1/images/get")
    fun getImages(@Query("link") articleLink:String): Observable<DataBean<List<ImageDataItem>>>
}