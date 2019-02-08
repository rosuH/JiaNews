package me.rosuh.jianews.bean

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

/**
 * 这个类是 ArticleBean 文章类，描述了 ArticleBean 这个类的成员和方法
 * @author rosuh 2018-5-9
 * @version 0.1
 */
data class ArticleBean(
    var id: Int, var url: String, var title: String, var summary: String,
    var thumbnail: String, var content: String,
    var date: String, var type: String, var views: Int, var isRead: Boolean,
    var imagesList: List<String>
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readInt(),
        1 == source.readInt(),
        source.createStringArrayList()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(url)
        writeString(title)
        writeString(summary)
        writeString(thumbnail)
        writeString(content)
        writeString(date)
        writeString(type)
        writeInt(views)
        writeInt((if (isRead) 1 else 0))
        writeStringList(imagesList)
    }

    companion object {
        @JvmField
        var CREATOR: Parcelable.Creator<ArticleBean> = object : Parcelable.Creator<ArticleBean> {
            override fun createFromParcel(source: Parcel): ArticleBean = ArticleBean(source)
            override fun newArray(size: Int): Array<ArticleBean?> = arrayOfNulls(size)
        }
    }
}
