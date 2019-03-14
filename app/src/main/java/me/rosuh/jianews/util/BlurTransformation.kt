package me.rosuh.jianews.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 *
 * @author rosuh
 * @date 2019/3/13
 */
class BlurTransformation(val context:Context, val blurRadius:Float): BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return blurBitmap(context, toTransform,  blurRadius, outWidth, outHeight)
    }

    private fun blurBitmap(context: Context, image:Bitmap, blurRadius: Float, outWidth: Int, outHeight: Int):Bitmap{
        val inputBitmap = Bitmap.createScaledBitmap(image, outWidth, outHeight, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val renderScript =  RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap)
        blurScript.setRadius(blurRadius)
        blurScript.setInput(tmpIn)
        blurScript.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap

    }
}