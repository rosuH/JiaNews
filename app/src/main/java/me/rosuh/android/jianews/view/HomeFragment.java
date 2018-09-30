package me.rosuh.android.jianews.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import me.rosuh.android.jianews.R;
import me.rosuh.android.jianews.util.Const;

/**
 * @author rosu
 * @date 2018/9/29
 */
public class HomeFragment extends Fragment {
    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);


        ViewPager mViewPager = view.findViewById(R.id.vp_article_list);

//         文章列表
        FragmentPagerAdapter mStatePagerAdapter = new FragmentPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager()){

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

        initTabLayout(mViewPager, view);


        return view;
    }

    /**
     * 初始化导航栏
     * @param mViewPager 传入设置好的 ViewPager 和 TabLayout 关联起来
     */
    private void initTabLayout(ViewPager mViewPager, View view){
        TabLayout tabLayout = view.findViewById(R.id.tb_layout_nav);

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
}
