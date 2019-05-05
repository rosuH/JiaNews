package me.rosuh.jianews.bean

/**
 *
 * @author rosuh
 * @date 2019/5/4
 */
data class User(
    var account: String,
    var passwd:String,
    var name: String = "",
    var avatar: String = "",
    var created: String = "",
    var description: String = "",
    var id: String = "",
    var updated: String = "",
    var user_group: String = ""
)