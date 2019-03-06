package me.rosuh.jianews.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html.ImageGetter
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.rosuh.android.jianews.R
import java.lang.Exception

/**
 *
 * @author rosuh
 * @date 2019/1/11
 */
class TextViewImageGetter(private val context: Context, private val scope: CoroutineScope, private val tv: TextView): ImageGetter, CoroutineScope by scope{

    override fun getDrawable(source: String?): Drawable {
        val listDrawable = LevelListDrawable()
        val drawable = context.resources.getDrawable(R.drawable.rec_loading, context.theme)
        listDrawable.addLevel(0, 0, drawable)
        listDrawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        launch(Dispatchers.Default){
            try {
                doRequest(source, listDrawable)
            }catch (e:Exception){
//                launch(Dispatchers.Main){
//                    Toast.makeText(context, "获取图片失败", Toast.LENGTH_SHORT).show()
//                }
            }
        }
        return listDrawable
    }

    private fun doRequest(source: String?, listDrawable: LevelListDrawable){
        val screenWidth = ViewUtils.getScreenWidthInPixel(context)
        val drawable = GlideApp
            .with(context)
            .load(source)
            .override(screenWidth, screenWidth/4*3)
            .fitCenter()
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