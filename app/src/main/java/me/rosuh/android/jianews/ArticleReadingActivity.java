package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * 这个类是文章阅读页面的 AppCompatActivity 类
 * @author rosuh 2018-5-9
 * @version 0.1
 */

public class ArticleReadingActivity extends AppCompatActivity {

    private ArticleBean mArticleBean;
    public static Intent newIntent(ArticleBean articleBean, Activity activity){
        Intent intent = new Intent(activity, ArticleReadingActivity.class);
        intent.putExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM, articleBean);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_reading_activity);
        Toolbar mToolbar = findViewById(R.id.tb_reading);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebView webView = findViewById(R.id.wv_reading);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        String imageFixStr = "<style>img{display: inline;height: auto;max-width: 100%;}</style>";
        mArticleBean = getIntent().getParcelableExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM);
        if (mArticleBean != null){
            webView.loadDataWithBaseURL(null, imageFixStr + mArticleBean.getContent()
                    , "text/html", "UTF-8", null);
        }else {
            webView.loadDataWithBaseURL(null, imageFixStr + getString(R.string.content_not_found)
                    , "text/html", "UTF-8", null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.article_reading_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_link:
                // 使用浏览器打开文章原始链接
                Intent intentLink = new Intent(Intent.ACTION_VIEW);
                intentLink.setData(Uri.parse(mArticleBean.getUrl()));
                startActivity(intentLink);
                break;
            case R.id.menu_item_share:
                // 分享按钮
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.putExtra(Intent.EXTRA_TEXT, mArticleBean.getTitle() + "\n" + mArticleBean.getUrl());
                intentShare.setType("text/plain");
                startActivity(Intent.createChooser(intentShare, getResources().getString(R.string.menu_item_share)));
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
