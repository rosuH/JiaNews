package me.rosuh.jianews.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.rosuh.android.jianews.R;
import me.rosuh.jianews.util.GlideApp;

/**
 * 首页 Activity
 * @author rosu
 */
public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mDrawerLayout = findViewById(R.id.dl_home);

        initToolBar();
        initNavigationView();

        Fragment homeFragment = getSupportFragmentManager()
                .findFragmentById(R.id.home_fragment_layout);

        if (homeFragment == null){
            homeFragment = HomeFragment.Companion.getInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_fragment, homeFragment)
                    .commit();
        }
    }

    /**
     * 初始化顶部工具栏
     */
    private void initToolBar(){
        Toolbar toolbar = findViewById(R.id.tb_home);
        setSupportActionBar(toolbar);
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
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        ImageView imageView = headerView.findViewById(R.id.iv_nav_header);
        imageView.setOnClickListener(v -> {
            ImageView iv = new ImageView(HomeActivity.this);
            iv.setBackgroundColor(Color.parseColor("#99000000"));
            GlideApp.with(this)
                    .load(R.drawable.easter_egg)
                    .into(iv);
            if (iv.getParent() != null){
                ((ViewGroup)iv.getParent()).removeView(iv);
            }
            iv.animate().alpha(1).setDuration(1000).start();
            iv.setOnClickListener(v1 -> {
                iv.setImageAlpha(0);
                if (iv.getParent() != null){
                    ((ViewGroup)iv.getParent()).removeView(iv);
                }
            });
            HomeActivity.this.addContentView(iv, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_item_about:
                    startActivity(new Intent(HomeActivity.this, AboutPageDialog.class));
                    break;
                default:
            }
            mDrawerLayout.closeDrawers();
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_activity, menu);
        MenuItem item = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setVisibility(View.INVISIBLE);
        searchView.setQueryHint("请输入关键词");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HomeActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
