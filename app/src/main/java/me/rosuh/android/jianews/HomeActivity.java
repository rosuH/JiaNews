package me.rosuh.android.jianews;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.view.WindowManager;
import android.webkit.WebView;
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

    private DrawerLayout mDrawerLayout;
    private Fragment mFragmentManager;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private static final String TAG = "APFragment";

    private FragmentPagerAdapter mStatePagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mViewPager = findViewById(R.id.vp_article_list);
        mTabLayout = findViewById(R.id.tb_layout_nav);
        mDrawerLayout = findViewById(R.id.dl_home);
        final NavigationView mNavigationView = findViewById(R.id.nav_view);

        // 顶部工具栏
        Toolbar mToolbar = findViewById(R.id.tb_home);
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
                switch (item.getItemId()){
                    case R.id.nav_item_about:
                        startActivity(new Intent(HomeActivity.this, AboutPageDialog.class));
                        break;
                    default:
                }
                if (item.isChecked()){
                    item.setChecked(false);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        // 添加轮播图
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.fl_banner_container, BannerFragment.newInstance(), TAG_FRAGMENT_BANNER);
        ft.commit();

        // 底部导航栏
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());

        // 文章列表
        mFragmentManager = getSupportFragmentManager();
        mStatePagerAdapter = new FragmentPagerAdapter(mFragmentManager){

            @Override
            public Fragment getItem(int position) {
                return  ArticleListFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return Const.VALUE_ARTICLE_MAX_PAGES;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return PagerAdapter.POSITION_NONE;
            }
        };
        mViewPager.setAdapter(mStatePagerAdapter);
        mViewPager.setOffscreenPageLimit(1);


        mViewPager.setCurrentItem(Const.VALUE_ARTICLE_START_PAGE);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        setTabLayout();
    }

    private void setTabLayout(){
        mTabLayout.getTabAt(0).setText(R.string.tab_home)
                .setContentDescription(R.string.tab_home).setIcon(R.drawable.ic_home_button);
        mTabLayout.getTabAt(1).setText(R.string.tab_announce)
                .setContentDescription(R.string.tab_announce).setIcon(R.drawable.ic_announce_button);
        mTabLayout.getTabAt(2).setText(R.string.tab_activity)
                .setContentDescription(R.string.tab_activity).setIcon(R.drawable.ic_campus_button);
        mTabLayout.getTabAt(3).setText(R.string.tab_media)
                .setContentDescription(R.string.tab_media).setIcon(R.drawable.ic_media_button);
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
