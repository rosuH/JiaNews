package me.rosuh.jianews.bean


data class ArticleDataItem(
    val img: Int = 0,
    val created: String = "",
    val link: String = "",
    val id: Int = 0,
    val title: String = "",
    val type: String = "",
    val content: String = "",
    val views: Int = 0,
    var imageList: List<ImageDataItem>
)