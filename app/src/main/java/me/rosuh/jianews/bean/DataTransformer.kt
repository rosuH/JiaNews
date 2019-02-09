package me.rosuh.jianews.bean

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.rosuh.jianews.network.ImageService
import me.rosuh.jianews.util.ResponseThrowable

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
class DataTransformer {

    companion object {

        /**
         * 切换线程
         */
        @JvmStatic
        fun <T> swicthSchedulers(): ObservableTransformer<T, T> {
            return ObservableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            }
        }

        /**
         * 从响应消息中拿到数据或错误信息
         * @param needThrow 表示是否需要显式抛出错误
         */
        @JvmStatic
        fun <T> getDataFromResponse(needThrow: Boolean, isImage:Boolean = false): ObservableTransformer<DataBean<T>, T> {
            return ObservableTransformer { upstream ->
                upstream
                    .flatMap {
                        if (it.isError() && needThrow) {
                            Observable.error(ResponseThrowable(message = it.msg, code = it.code))
                        } else if (it.isError()) {
                            Observable.empty()
                        } else {
                            Observable.just(it.data)
                        }
                    }
            }
        }

        /**
         * 传入 @param imageService，用于发起网络请求，以便填充上下文（ArticleDataItem）的图片链接列表
         */
        @JvmStatic
        fun getImageList(imageService: ImageService): ObservableTransformer<List<ArticleDataItem>, ArticleDataItem> {
            return ObservableTransformer { upstream ->
                upstream
                    .flatMap {
                        Observable.fromIterable(it)
                    }
                    .flatMap {
                        if (it.img == 1) {
                            imageService
                                .getImages(articleLink = it.link)
                                .compose(DataTransformer.getDataFromResponse(needThrow = false))
                                .flatMap { imageItems ->
                                    it.imageList = imageItems
                                    Observable.just(it)
                                }
                        } else {
                            Observable.just(it)
                        }
                    }
            }
        }
    }
}
