package me.rosuh.jianews.util

import me.rosuh.jianews.util.Const.*

/**
 * @author rosu
 * @date 2018/11/7
 */
object StringUtils{
    /**
     * 通过枚举变量返回对应的链接
     */
    fun getCorrectUrl(pageURL: Const.PageURL):String{
        when(pageURL){
            Const.PageURL.URL_MEDIA_REPORTS -> return URL_MEDIA_REPORTS;
            Const.PageURL.URL_CAMPUS_ACTIVITIES -> return URL_CAMPUS_ACTIVITIES;
            Const.PageURL.URL_CAMPUS_ANNOUNCEMENT -> return URL_CAMPUS_ANNOUNCEMENT;
            else -> return URL_MAJOR_NEWS;
        }
    }
}
