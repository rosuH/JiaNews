package me.rosuh.jianews.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import me.rosuh.jianews.view.ArticleListFragment;

/**
 * @author rosua
 * @date 2018/10/5
 */
public class FragmentPagerAdapterWrapper extends FragmentPagerAdapter {
    public FragmentPagerAdapterWrapper(FragmentManager fm) {
        super(fm);
    }

    private SparseArray<ArticleListFragment> mArticleListFragmentMap = new SparseArray<>();
    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        mArticleListFragmentMap.put(position, (ArticleListFragment) object);
        return object;
    }

    @Override
    public Fragment getItem(int position) {
        ArticleListFragment articleListFragment = mArticleListFragmentMap.get(position);
        if (articleListFragment != null){
            return articleListFragment;
        }
        return  ArticleListFragment.getInstances(position);
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
