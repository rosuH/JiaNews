package me.rosuh.jianews.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import me.rosuh.jianews.view.ArticleListFragment;
import me.rosuh.jianews.view.HomeFragment;

/**
 * @author rosu
 * @date 2018/10/5
 */
public class FragmentPagerAdapterWrapper extends FragmentPagerAdapter {
    public FragmentPagerAdapterWrapper(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        return  ArticleListFragment.getInstances(Const.getCorrectURL(position));
    }

    @Override
    public int getCount() {
        return Const.VALUE_ARTICLE_MAX_PAGES;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
