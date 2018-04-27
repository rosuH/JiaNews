package me.rosuh.android.jianews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.rosuh.android.jianews.Const.TAG_FRAGMENT_ARTICLE_LIST;
import static me.rosuh.android.jianews.Const.TAG_FRAGMENT_BANNER;
import static me.rosuh.android.jianews.Const.TAG_FRAGMENT_TAB_NAV;

public class HomeActivity extends AppCompatActivity {


    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mBannerLayout;
    private FrameLayout mArticleListLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private List<Article> mArticles;
    private ArticleLab mArticleLab;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mBannerLayout = findViewById(R.id.fl_banner_container);
        mArticleListLayout = findViewById(R.id.fl_article_list_container);
        mDrawerLayout = findViewById(R.id.dl_home);
        mNavigationView = findViewById(R.id.nav_view);

        // 顶部工具栏
        mToolbar = findViewById(R.id.tb_home);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_home);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // 侧滑栏
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        // 添加轮播图、文章列表和底部导航栏
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.fl_banner_container, BannerFragment.newInstance(), TAG_FRAGMENT_BANNER);
        ft.add(R.id.fl_article_list_container, ArticleViewPagerFragment.newInstance()
                , TAG_FRAGMENT_ARTICLE_LIST);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

}
