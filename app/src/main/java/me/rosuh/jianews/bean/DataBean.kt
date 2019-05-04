package me.rosuh.jianews.bean

/**
 *
 * @author rosuh
 * @date 2019/2/6
 */
data class DataBean<T>(
    val code: Int,
    val msg: String,
    val data: T
) {

    fun isError(): Boolean {
        return this.code != 200
    }
}