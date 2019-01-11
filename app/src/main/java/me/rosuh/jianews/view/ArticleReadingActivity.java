package me.rosuh.jianews.view;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

import android.widget.TextView;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import me.rosuh.android.jianews.R;
import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.util.Const;
import me.rosuh.jianews.util.TextViewImageGetter;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.XMLReader;

/**
 * 这个类是文章阅读页面的 AppCompatActivity 类
 * @author rosuh 2018-5-9
 * @version 0.1
 */

public class ArticleReadingActivity extends AppCompatActivity implements CoroutineScope {

    @NotNull
    @Override
    public CoroutineContext getCoroutineContext() {
        return Dispatchers.getMain();
    }

    private ArticleBean mArticleBean;
    public static Intent newIntent(ArticleBean articleBean, Activity activity){
        Intent intent = new Intent(activity, ArticleReadingActivity.class);
        intent.putExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM, articleBean);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.article_reading_frag);
        Toolbar mToolbar = findViewById(R.id.tb_reading);
        TextView tvToolbarTitle = findViewById(R.id.tv_tool_bar_reading);
        TextView tvContent = findViewById(R.id.tv_article_content);
        mArticleBean = getIntent().getParcelableExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM);

        tvToolbarTitle.setText(mArticleBean.getTitle());
        tvToolbarTitle.setSelected(true);
        mToolbar.setSubtitle(mArticleBean.getDate());
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_back);
        }

        if (VERSION.SDK_INT >= VERSION_CODES.N){
            tvContent.setText(Html.fromHtml(mArticleBean.getContent(), FROM_HTML_MODE_COMPACT,
                    new TextViewImageGetter(this, this, tvContent), null));
        }else {
            tvContent.setText(Html.fromHtml(mArticleBean.getContent(), new TextViewImageGetter(this, this, tvContent), null));
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
