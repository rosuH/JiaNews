package me.rosuh.jianews.util

/**
 *
 * @author rosuh
 * @date 2019/2/9
 */
class ResponseThrowable(message: String?, code: Int?) : Throwable(message) {

    override val cause: Throwable?
        get() = super.cause
    override val message: String?
        get() = super.message
}