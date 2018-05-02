package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Scroller;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import static android.widget.RelativeLayout.BELOW;

public class ArticleReadingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private static final String TAG = "ArticleReadingActivity";
    public static Intent newIntent(Article article, Activity activity){
        Intent intent = new Intent(activity, ArticleReadingActivity.class);
        intent.putExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM, article);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_reading_activity);
        mToolbar = findViewById(R.id.tb_reading);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TextView textView = findViewById(R.id.tv_reading);

        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            private Drawable mDrawable;
            private SimpleTarget<Drawable> mSimpleTarget = new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    mDrawable = resource;
                    textView.invalidateDrawable(mDrawable);
                }
            };
            @Override
            public Drawable getDrawable(String source) {
                Glide.with(ArticleReadingActivity.this).load(source).into(mSimpleTarget);
                return mDrawable;
            }

        };

        Article article = getIntent().getParcelableExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM);
        if (article != null){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N ){
                textView.setText(Html.fromHtml(article.getContent(), imageGetter, null));
            }else {
                textView.setText(Html.fromHtml(article.getContent(), 123, imageGetter, null));
            }
        }else {
            textView.setText(R.string.content_not_found);
        }
    }
}
