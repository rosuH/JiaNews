package me.rosuh.android.jianews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * @author rosuh
 */
public class BannerPageFragment extends Fragment {
    private Context mContext;

    public static Fragment newInstance(int position, Article article){
        Bundle args = new Bundle();
        args.putInt(Const.KEY_ARGS_BANNER_POSITION, position);
        args.putParcelable(Const.KEY_ARGS_BANNER_ARTICLE, article);
        Fragment fragment = new BannerPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.banner_pager_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.iv_banner_image);
        TextView textView = view.findViewById(R.id.tv_banner_title);
        // 获取传递来的数据
        int position = getArguments().getInt(Const.KEY_ARGS_BANNER_POSITION);
        final Article article = getArguments().getParcelable(Const.KEY_ARGS_BANNER_ARTICLE);

        if (article == null){
            // 如果为空，不加载 imageview
            imageView.setVisibility(View.GONE);
            textView.setText(R.string.item_loading);
        }else {
            // 不为空则从图片链接加载，并设置 text
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(article.getThumbnail())
                    .apply(bitmapTransform(new BlurTransformation(3, 3)))
                    .into(imageView);
            textView.setText(article.getTitle());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ArticleReadingActivity.newIntent(article, getActivity()));
                }
            });
        }
        return view;
    }
}
