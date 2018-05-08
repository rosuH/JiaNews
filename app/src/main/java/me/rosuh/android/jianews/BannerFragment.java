package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static me.rosuh.android.jianews.Const.VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE;
import static me.rosuh.android.jianews.Const.VALUE_BANNER_START_PAGE;


public class BannerFragment extends Fragment {


    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private ArticleLab mArticleLab;
    private List<Article> mArticles = new ArrayList<>();
    private List<Article> mArticlesSync = new ArrayList<>();
    private ScheduledExecutorService mBannerExecutor;
    private List<TextView> mIndicatorTextViews = new ArrayList<>();
    private FragmentStatePagerAdapter mFragmentPagerAdapter;
    private Activity mActivity;
    private static final String TAG = "BannerFragment";
    private Context mContext;

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
        mFragmentPagerAdapter = new FragmentStatePagerAdapter(fm){
            private int mIndex;
            @Override
            public Fragment getItem(int position) {
                mIndex = getRightIndex(position);
                // 如果未获取到文章数据，则传送一个 null 过去
                // 目标收到后进行判断，如果是 null，则设置为 加载图
                Log.d(TAG, "getItem: Call");
                if (ismArticlesEmpty(mArticles)){
                    return BannerPageFragment.newInstance(mIndex, null);
                }else {
                    return BannerPageFragment.newInstance(mIndex, mArticles.get(mIndex));
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

    private int getRightIndex(int position){
        int tempNum = VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE;
        if (!ismArticlesEmpty(mArticles)){
            tempNum = mArticles.size();
        }
        int index = Math.abs(position - VALUE_BANNER_START_PAGE) % tempNum;
        if (position < VALUE_BANNER_START_PAGE && index != 0){
            index = tempNum - index;
        }

        return index;
    }

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

    private void initCircle(){
        int width = 15;
        int height = 15;
        int margin = 5;
        int loopSize;
        if (ismArticlesEmpty(mArticles)){
            loopSize = 10;
        }else {
           loopSize = mArticles.size();
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
        mBannerExecutor.scheduleAtFixedRate(command, 3, 3, TimeUnit.SECONDS);
    }

    /**
     * 功能：停止轮播图轮播功能
     */
    private void stopBannerScroll(){
        if (mBannerExecutor != null){
            mBannerExecutor.shutdownNow();
        }
    }

    private static class GetDataTask extends AsyncTask<Integer, Void, List<Article>> {
        private WeakReference<BannerFragment> mWeakReference;
        private ArticleLab mArticleLab;

        GetDataTask(BannerFragment context){
            mWeakReference = new WeakReference<>(context);
            mArticleLab = mWeakReference.get().mArticleLab;
        }

        @Override
        protected List<Article> doInBackground(Integer...integers) {
            return mArticleLab.getArticleList(Const.URL_HOME_PAGE, Const.VALUE_ARTICLE_INDEX_START);
        }
        @Override
        protected void onPostExecute(List<Article> result) {
            super.onPostExecute(result);
            mWeakReference.get().mArticles = result;
        }
    }


    /**
     * 功能：若 mArticles 无意义，则 ismArticlesEmpty 为真
     * @return 若 mArticles 无意义，则 ismArticlesEmpty 为真
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
