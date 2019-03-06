package me.rosuh.jianews.util

import android.content.pm.PackageManager
import android.icu.util.LocaleData
import android.os.Build
import me.rosuh.jianews.util.Const.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * @author rosu
 * @date 2018/11/7
 */
class StringUtils{
    companion object {
        val ft by lazy {
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        }
        /**
         * 通过枚举变量返回对应的链接
         */
        @JvmStatic
        fun getCorrectUrl(pageURL: Const.PageURL):String{
            return when(pageURL){
                Const.PageURL.URL_MEDIA_REPORTS -> URL_MEDIA_REPORTS
                Const.PageURL.URL_CAMPUS_ANNOUNCEMENT -> URL_CAMPUS_ANNOUNCEMENT
                else -> URL_MAJOR_NEWS
            }
        }

        @JvmStatic
        fun getFormattedTime(dateString: String):String{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                LocalDate.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME).toString()
            }else {
                ft.format(Date(dateString))
            }
        }
    }
}
