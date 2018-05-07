package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Article mArticle;
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

        WebView webView = findViewById(R.id.wv_reading);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        String imageFixStr = "<style>img{display: inline;height: auto;max-width: 100%;}</style>";
        mArticle = getIntent().getParcelableExtra(Const.KEY_INTENT_ARTICLE_READING_ITEM);
        if (mArticle != null){
            webView.loadDataWithBaseURL(null, imageFixStr + mArticle.getContent()
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
                Intent intentLink = new Intent(Intent.ACTION_VIEW);
                intentLink.setData(Uri.parse(mArticle.getUrl()));
                startActivity(intentLink);
                break;
            case R.id.menu_item_share:
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.putExtra(Intent.EXTRA_TEXT, mArticle.getTitle() + "\n" + mArticle.getUrl());
                intentShare.setType("text/plain");
                startActivity(Intent.createChooser(intentShare, getResources().getString(R.string.menu_item_share)));
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
