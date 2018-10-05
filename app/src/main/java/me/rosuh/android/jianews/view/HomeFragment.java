package me.rosuh.android.jianews.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.rosuh.android.jianews.R;
import me.rosuh.android.jianews.util.Const;
import me.rosuh.android.jianews.util.FragmentPagerAdapterWrapper;

/**
 * @author rosu
 * @date 2018/9/29
 */
public class HomeFragment extends Fragment {
    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    FragmentPagerAdapter mStatePagerAdapter;
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);


        ViewPager viewPager = view.findViewById(R.id.vp_article_list);

//         文章列表
        mStatePagerAdapter = new FragmentPagerAdapterWrapper(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
        viewPager.setAdapter(mStatePagerAdapter);
        viewPager.setCurrentItem(Const.VALUE_ARTICLE_START_PAGE);

        initTabLayout(viewPager, view);

        return view;
    }

    /**
     * 初始化导航栏
     * @param viewPager 传入设置好的 ViewPager 和 TabLayout 关联起来
     */
    private void initTabLayout(final ViewPager viewPager, View view){
        TabLayout tabLayout = view.findViewById(R.id.tb_layout_nav);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            private Long sDoubleClickedInterval = 400L;
            private long mLastSelectedTime;
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                Long newestSelectedTime = System.currentTimeMillis();
                if (newestSelectedTime - mLastSelectedTime < sDoubleClickedInterval){
                    int tabPos = tab.getPosition();
                    FragmentPagerAdapter adapter = (FragmentPagerAdapter)viewPager.getAdapter();
                    ArticleListFragment articleListFragment = (ArticleListFragment) adapter.getItem(tabPos);
                    articleListFragment.scrollToTop();
                }
                mLastSelectedTime = newestSelectedTime;
            }
        });

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.tab_home)
                .setContentDescription(R.string.tab_home);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(R.string.tab_announce)
                .setContentDescription(R.string.tab_announce);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText(R.string.tab_activity)
                .setContentDescription(R.string.tab_activity);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setText(R.string.tab_media)
                .setContentDescription(R.string.tab_media);
    }
}
