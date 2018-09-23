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
import android.view.Menu;
import android.view.MenuItem;

import java.util.Objects;

import static me.rosuh.android.jianews.Const.TAG_FRAGMENT_BANNER;

/**
 * 首页 Activity
 * @author rosu
 */
public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        ViewPager mViewPager = findViewById(R.id.vp_article_list);
        mDrawerLayout = findViewById(R.id.dl_home);

        initToolBar();
        initNavigationView();
        
//         添加轮播图
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
//        ft.add(R.id.fl_banner_container, BannerFragment.newInstance(), TAG_FRAGMENT_BANNER);
//        ft.commit();
        
//         文章列表
        mFragmentManager = getSupportFragmentManager();
        FragmentPagerAdapter mStatePagerAdapter = new FragmentPagerAdapter(mFragmentManager){

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

        initTabLayout(mViewPager);
    }

    /**
     * 初始化顶部工具栏
     */
    private void initToolBar(){
        Toolbar mToolbar = findViewById(R.id.tb_home);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_home);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * 初始化侧滑栏
     */
    private void initNavigationView(){
        // 侧滑栏
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
    }

    /**
     * 初始化底部导航栏
     * @param mViewPager 传入设置好的 ViewPager 和 TabLayout 关联起来
     */
    private void initTabLayout(ViewPager mViewPager){
        TabLayout tabLayout = findViewById(R.id.tb_layout_nav);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.tab_home)
                .setContentDescription(R.string.tab_home);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(R.string.tab_announce)
                .setContentDescription(R.string.tab_announce);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText(R.string.tab_activity)
                .setContentDescription(R.string.tab_activity);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setText(R.string.tab_media)
                .setContentDescription(R.string.tab_media);
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
