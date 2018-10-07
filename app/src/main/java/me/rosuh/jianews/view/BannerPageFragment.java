package me.rosuh.jianews.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.rosuh.jianews.ArticleReadingActivity;
import me.rosuh.android.jianews.R;
import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.util.Const;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * 本类是轮播图具体页面的 fragment 类
 * @author rosuh
 */
public class BannerPageFragment extends Fragment {
    private Context mContext;

    /**
     * @param articleBean 和 page 相对应的文章对象
     * @return 附带文章对象的 fragment
     */
    public static Fragment newInstance(ArticleBean articleBean){
        Bundle args = new Bundle();
        args.putParcelable(Const.KEY_ARGS_BANNER_ARTICLE, articleBean);
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
        final ArticleBean articleBean = getArguments().getParcelable(Const.KEY_ARGS_BANNER_ARTICLE);

        if (articleBean == null){
            // 如果为空，不加载 imageview
            imageView.setVisibility(View.GONE);
            textView.setText(R.string.item_loading);
        }else {
            // 不为空则从图片链接加载，并设置 text
            imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(articleBean.getThumbnail())
                    .apply(bitmapTransform(new BlurTransformation(3, 3)))
                    .into(imageView);
            textView.setText(articleBean.getTitle());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ArticleReadingActivity.newIntent(articleBean, getActivity()));
                }
            });
        }
        return view;
    }
}
