package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

import static me.rosuh.android.jianews.Const.VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE;
import static me.rosuh.android.jianews.Const.VALUE_BANNER_START_PAGE;

/**
 * 这个类是轮播图的 fragment 类
 *
 * @author rosuh 2018-5-9
 * @version 0.1
 */
public class BannerFragment extends Fragment {
    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private ArticleLab mArticleLab;
    private List<ArticleBean> mArticleBeans = new ArrayList<>();
    private ScheduledExecutorService mBannerExecutor;
    private List<TextView> mIndicatorTextViews = new ArrayList<>();
    private Activity mActivity;
    private Context mContext;

    /**
     * New instance fragment.
     *
     * @return the fragment
     */
    public static Fragment newInstance() {
        return new BannerFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleLab = ArticleLab.get(mContext);
        getData(mArticleLab);
        GetDataTask getDataTask = new GetDataTask(BannerFragment.this);
        getDataTask.execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.banner_fragment, container, false);
        mActivity = getActivity();
        mViewPager = view.findViewById(R.id.vp_banner);
        mLinearLayout = view.findViewById(R.id.ll_indicator);

        // 初始化指示器
        initCircle();

        FragmentManager fm = getFragmentManager();
        FragmentStatePagerAdapter mFragmentPagerAdapter = new FragmentStatePagerAdapter(fm){
            private int mIndex;
            @Override
            public Fragment getItem(int position) {
                mIndex = getRightIndex(position);
                // 如果未获取到文章数据，则传送一个 null 过去
                // 目标收到后进行判断，如果是 null，则设置为 加载图
                if (ismArticlesEmpty(mArticleBeans)){
                    return BannerPageFragment.newInstance(null);
                }else {
                    return BannerPageFragment.newInstance(mArticleBeans.get(mIndex));
                }
            }

            @Override
            public int getCount() {
                return Const.VALUE_BANNER_MAX_PAGES;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return PagerAdapter.POSITION_NONE;
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setCurrentItem(VALUE_BANNER_START_PAGE);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            private int mIndex;
            @Override
            public void onPageSelected(int position) {
                mIndex = getRightIndex(position);
                changePoints(mIndex);
                startBannerScroll();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        // 通过反射改变轮播图的切换速度
        try {
            Class refClass = Class.forName("android.support.v4.view.ViewPager");
            Field f = refClass.getDeclaredField("mScroller");
            FixedSpeedScroller fixedSpeedScroller = new FixedSpeedScroller(getContext(), new LinearOutSlowInInterpolator());
            fixedSpeedScroller.setmDuration(1300);
            f.setAccessible(true);
            f.set(mViewPager, fixedSpeedScroller);
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }


    /**
     * 功能：把 page 的当前位置转换为正确的索引值，以便从集合中获取数据
     * @param position  page 的原生位置
     * @return  返回正确的索引值
     */
    private int getRightIndex(int position){
        int tempNum = VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE;
        if (!ismArticlesEmpty(mArticleBeans)){
            tempNum = mArticleBeans.size();
        }
        int index = Math.abs(position - VALUE_BANNER_START_PAGE) % tempNum;
        if (position < VALUE_BANNER_START_PAGE && index != 0){
            index = tempNum - index;
        }

        return index;
    }


    /**
     * 功能：通过当前页面的 position 来改变对应的指示器的样式
     * @param pos 当前 page 的位置
     */
    private void changePoints(int pos){
        if (mIndicatorTextViews != null){
            for (int i = 0; i < mIndicatorTextViews.size(); i++){
                if (pos == i){
                    mIndicatorTextViews.get(i).setBackgroundResource(R.drawable.dot_selected);
                }else {
                    mIndicatorTextViews.get(i).setBackgroundResource(R.drawable.dot_normal);
                }
            }
        }
    }


    /**
     * 功能：初始化指示器
     */
    private void initCircle(){
        int width = 15;
        int height = 15;
        int margin = 5;
        int loopSize;
        if (ismArticlesEmpty(mArticleBeans)){
            loopSize = 10;
        }else {
           loopSize = mArticleBeans.size();
        }

        for (int i = 0 ; i < loopSize; i++){
            TextView textView = new TextView(this.getContext());
            if (i == 0){
                textView.setBackgroundResource(R.drawable.dot_selected);
            }else {
                textView.setBackgroundResource(R.drawable.dot_normal);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);
            mIndicatorTextViews.add(textView);
            mLinearLayout.addView(textView);
        }

    }

    /**
     * 功能：开始轮播图轮播功能
     */
    private void startBannerScroll(){
        stopBannerScroll();
        mBannerExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable command = new Runnable() {
            @Override
            public void run() {
                selectNextItem();
            }

            private void selectNextItem(){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                        if (mViewPager.getCurrentItem() == Const.VALUE_BANNER_MAX_PAGES){
                            mViewPager.setCurrentItem(Const.VALUE_BANNER_START_PAGE);
                        }
                    }
                });
            }
        };
        mBannerExecutor.scheduleAtFixedRate(command, 5, 3, TimeUnit.SECONDS);
    }

    /**
     * 功能：停止轮播图轮播功能
     */
    private void stopBannerScroll(){
        if (mBannerExecutor != null){
            mBannerExecutor.shutdownNow();
        }
    }

    public static void getData(ArticleLab articleLab){
//        Observable
//                .just(articleLab)


    }

    private static class GetDataTask extends AsyncTask<Integer, Void, List<ArticleBean>> {
        private WeakReference<BannerFragment> mWeakReference;
        private ArticleLab mArticleLab;

        /**
         * Instantiates a new Get data task.
         *
         * @param context the context
         */
        GetDataTask(BannerFragment context){
            mWeakReference = new WeakReference<>(context);
            mArticleLab = mWeakReference.get().mArticleLab;
        }

        @Override
        protected List<ArticleBean> doInBackground(Integer...integers) {
            return mArticleLab.getArticleList(Const.URL_HOME_PAGE, Const.VALUE_ARTICLE_INDEX_START);
        }
        @Override
        protected void onPostExecute(List<ArticleBean> result) {
            super.onPostExecute(result);
            mWeakReference.get().mArticleBeans = result;
        }
    }


    /**
     * 功能：若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     * @return 若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     */
    private boolean ismArticlesEmpty(List list){
        return (list == null || list.isEmpty());
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBannerScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBannerScroll();
    }
}
