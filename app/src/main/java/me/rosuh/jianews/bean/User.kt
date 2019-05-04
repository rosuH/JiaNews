package me.rosuh.jianews.bean

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
data class User(
    var id:String = "-1",
    var account:String,
    var passwd:String,
    var name:String = "",
    var avatar:String = "",
    var description:String = ""
)