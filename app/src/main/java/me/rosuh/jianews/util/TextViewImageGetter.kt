package me.rosuh.jianews.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html.ImageGetter
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.rosuh.android.jianews.R

/**
 *
 * @author rosuh
 * @date 2019/1/11
 */
class TextViewImageGetter(private val context: Context, private val scope: CoroutineScope, private val tv: TextView): ImageGetter, CoroutineScope by scope{

    companion object {
        const val BASE_URL = "http://www.jyu.edu.cn/"
    }

    override fun getDrawable(source: String?): Drawable {
        val listDrawable = LevelListDrawable()
        val drawable = context.resources.getDrawable(R.drawable.ic_img_placeholder, context.theme)
        listDrawable.addLevel(0, 0, drawable)
        listDrawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        launch(Dispatchers.Default){
            println("Launch coroutine scope 1")
            doRequest(source, listDrawable)
        }
        return listDrawable
    }

    private fun doRequest(source: String?, listDrawable: LevelListDrawable) {
        val drawable = Glide
            .with(context)
            .load(BASE_URL + source)
            .submit().get()
        launch(scope.coroutineContext){
            listDrawable.addLevel(1, 1, drawable)
            listDrawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            listDrawable.level = 1
            val str = tv.text
            tv.text = str
        }
    }
}