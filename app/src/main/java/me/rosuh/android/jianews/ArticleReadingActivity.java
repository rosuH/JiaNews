package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;

public class ArticleReadingActivity extends AppCompatActivity {

    private WebView mWebView;
    private Toolbar mToolbar;
    private static final String TAG = "ArticleReadingActivity";
    public static Intent newIntent(Article article, Activity activity){
        Intent intent = new Intent(activity, ArticleReadingActivity.class);
        intent.putExtra(Const.KEY_Intent_ARTICLE_READING_ITEM, article);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_reading_fragment);
        mWebView = findViewById(R.id.wv_article_reading);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mToolbar = findViewById(R.id.tb_reading);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Article article = getIntent().getParcelableExtra(Const.KEY_Intent_ARTICLE_READING_ITEM);
        if (article != null){
            mWebView.loadData(article.getContent(), "text/html", "UTF-8");
        }else {
            mWebView.loadData(getString(R.string.content_not_found), "text/html", "UTF-8");

        }
        Log.d(TAG, "onCreate: article.getcontent = " + article.getContent());
    }
}
