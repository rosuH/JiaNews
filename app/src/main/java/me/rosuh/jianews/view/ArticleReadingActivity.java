package me.rosuh.jianews.view;

import static android.text.Html.FROM_HTML_MODE_COMPACT;
import static android.text.Html.FROM_HTML_MODE_LEGACY;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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
 *
 * @author rosuh 2018-5-9
 * @version 0.1
 */

public class ArticleReadingActivity extends AppCompatActivity
        implements CoroutineScope, PopupMenu.OnMenuItemClickListener, PopupMenu.OnDismissListener {

    private PopupMenu mFontPopupMenu;

    private TextView tvContent;

    @NotNull
    @Override
    public CoroutineContext getCoroutineContext() {
        return Dispatchers.getMain();
    }

    private ArticleBean mArticleBean;

    private View fontItemView;

    public static Intent newIntent(ArticleBean articleBean, Activity activity) {
        Intent intent = new Intent(activity, ArticleReadingActivity.class);
        intent.putExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM, articleBean);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.article_reading_frag);
        mArticleBean = getIntent().getParcelableExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM);

        Toolbar mToolbar = findViewById(R.id.tb_reading);
        tvContent = findViewById(R.id.tv_article_content);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_back);
        }
        tvContent.setText(produceContent(mArticleBean));
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private Spanned produceContent(ArticleBean articleBean) {
        if (articleBean == null) {
            return null;
        }

        String tmpStr = articleBean.getContent();
        for (String link : articleBean.getImagesList()) {
            tmpStr = tmpStr.concat(
                    "<img src=\"" + link + "\" width=\"600\"/><br/><br/>"
            );
        }
        articleBean.setContent(tmpStr);

        Spannable contentSpanned;
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            contentSpanned = (Spannable) Html.fromHtml(
                    "<h1>" + mArticleBean.getTitle() + "</h1><br/>" + mArticleBean.getContent(),
                    FROM_HTML_MODE_LEGACY,
                    new TextViewImageGetter(this, this, tvContent), null
            );
        } else {
            contentSpanned = (Spannable) Html.fromHtml(
                    "<h1>" + mArticleBean.getTitle() + "</h1><br/>" +
                            mArticleBean.getContent(),
                    new TextViewImageGetter(this, this, tvContent),
                    null
            );
        }

        for (ImageSpan span : contentSpanned.getSpans(0, contentSpanned.length(), ImageSpan.class)) {
            int flags = contentSpanned.getSpanFlags(span);
            int start = contentSpanned.getSpanStart(span);
            int end = contentSpanned.getSpanEnd(span);

            contentSpanned.setSpan(new URLSpan(span.getSource()) {
                @Override
                public void onClick(final View widget) {
                    super.onClick(widget);
                    Log.i("Image Clicked", "onClick: ===================" + widget.toString());
                }
            }, start, end, flags);
        }

        return contentSpanned;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.article_reading_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (fontItemView == null) {
            fontItemView = ArticleReadingActivity.this.findViewById(R.id.menu_item_font);
        }
        switch (item.getItemId()) {
            case R.id.menu_item_font:
                if (fontItemView == null) {
                    return false;
                }
                if (mFontPopupMenu == null) {
                    mFontPopupMenu = new PopupMenu(ArticleReadingActivity.this, fontItemView, Gravity.CENTER);
                    mFontPopupMenu.inflate(R.menu.reading_font_menu);
                    mFontPopupMenu.setOnMenuItemClickListener(this);
                    mFontPopupMenu.setOnDismissListener(this);
                }
                mFontPopupMenu.show();
                break;
            case R.id.menu_item_link:
                // 使用浏览器打开文章原始链接
                Intent intentLink = new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleBean.getUrl()));
                startActivity(Intent.createChooser(intentLink, "分享链接到..."));
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

    @Override
    public boolean onMenuItemClick(final MenuItem menuItem) {
        if (tvContent == null) {
            return false;
        }
        switch (menuItem.getItemId()) {
            case R.id.item_tiny_font:
                tvContent.setTextSize(15);
                break;
            case R.id.item_normal_font:
                tvContent.setTextSize(18);
                break;
            case R.id.item_large_font:
                tvContent.setTextSize(20);
                break;
        }
        menuItem.setChecked(true);
        return true;
    }

    @Override
    public void onDismiss(final PopupMenu popupMenu) {

    }
}
