package me.rosuh.jianews.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author rosuh
 * @date 2018/12/23
 */
@GlideExtension
public class MyGlideExtension {

    private MyGlideExtension() {
    }

    @NonNull
    @GlideOption

    public static RequestOptions getOptions(RequestOptions options, @NonNull Context context, int cornerRadius, int blurRadius) {
        int px = Math.round(cornerRadius * (context.getResources().getDisplayMetrics().xdpi
                / DisplayMetrics.DENSITY_DEFAULT));
        return options
                .transforms(new BlurTransformation(context, blurRadius))
                .transform(new RoundedCorners(px));
    }
}
