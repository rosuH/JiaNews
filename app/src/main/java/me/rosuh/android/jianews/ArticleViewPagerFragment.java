package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ArticleViewPagerFragment extends Fragment {
    private FragmentManager mFragmentManager;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private static final String TAG = "ArticleViewPagerFragmen";

    private FragmentStatePagerAdapter mStatePagerAdapter;

    public static Fragment newInstance(){
        return new ArticleViewPagerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_list_view_pager_fragment, container, false);
        mViewPager = view.findViewById(R.id.vp_article_list);
        mTabLayout = view.findViewById(R.id.tb_layout_nav);

        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());

        mFragmentManager = getFragmentManager();
        mViewPager.setAdapter(mStatePagerAdapter = new FragmentStatePagerAdapter(mFragmentManager){
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = ArticleListFragment.newInstance(position);
                fragment.setTargetFragment(ArticleViewPagerFragment.this, Const.REQUEST_CODE_ARTICLE_LIST_REFRESH);
                return fragment;
            }

            @Override
            public int getCount() {
                return Const.VALUE_ARTICLE_MAX_PAGES;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return PagerAdapter.POSITION_NONE;
            }
        });

        mViewPager.setCurrentItem(Const.VALUE_ARTICLE_START_PAGE);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        setTabLayout();
        return view;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_CODE_ARTICLE_LIST_REFRESH && resultCode == Activity.RESULT_OK){
            refreshViewpager();
        }
    }

    /**
     * 功能：刷新视图
     */
    private void refreshViewpager(){
        if (mViewPager != null && mFragmentManager != null){
            mStatePagerAdapter.notifyDataSetChanged();
            Log.d(TAG, "mStatePagerAdapter.notifyDataSetChanged() has been called ");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
