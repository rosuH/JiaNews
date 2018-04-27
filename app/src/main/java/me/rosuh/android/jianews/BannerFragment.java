package me.rosuh.android.jianews;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private ScheduledExecutorService mScheduledExecutorService;
    private Handler mHandler = new Handler();
//    private TimerRunner mTimerRunner;
    private List<TextView> mIndicatorTextViews = new ArrayList<>();
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private boolean isDataGot = false;


    public static Fragment newInstance() {
        return new BannerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleLab = ArticleLab.get(getContext());
        // 如果获取失败，则每个三秒重新获取一次，9 秒后失败则报告错误
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mArticles = mArticleLab.getBannerArticles();
                // 获取到数据则通知轮播图更新
                if (mArticles != null && !mArticles.isEmpty() && mFragmentPagerAdapter != null){
                    mFragmentPagerAdapter.notifyDataSetChanged();
                    stopScheduleRunner();
//                    isDataGot = true;
                }
            }
        },0, 3, TimeUnit.SECONDS);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.banner_fragment, container, false);
        mViewPager = view.findViewById(R.id.vp_banner);
        mLinearLayout = view.findViewById(R.id.ll_indicator);

        // 初始化指示器
        initCircle();

        FragmentManager fm = getFragmentManager();
        mFragmentPagerAdapter = new FragmentPagerAdapter(fm) {
            private int mIndex;
            @Override
            public Fragment getItem(int position) {
                // 如果 mArticles 为空，那么设置实际展示页面为默认大小值
                // 否则设置之为 mArticles.size()
                int tempNum = VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE;
                if (!ismArticlesEmpty()){
                    tempNum = mArticles.size();
                    stopScheduleRunner();
                }
                mIndex = Math.abs(position - VALUE_BANNER_START_PAGE) % tempNum;

                if (position < VALUE_BANNER_START_PAGE && mIndex != 0){
                    mIndex = tempNum - mIndex;
                }
                // 如果未获取到文章数据，则传送一个 null 过去
                // 目标收到后进行判断，如果是 null，则设置为 加载图
                if (ismArticlesEmpty()){
                    return BannerPageFragment.newInstance(mIndex, null);
                }else {
                    return BannerPageFragment.newInstance(mIndex, mArticles.get(mIndex));
                }
            }

            @Override
            public int getCount() {
                return Const.VALUE_BANNER_MAX_PAGES;
            }
        };

        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setCurrentItem(VALUE_BANNER_START_PAGE);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            private int mIndex;
            @Override
            public void onPageSelected(int position) {
                mIndex = Math.abs(position - VALUE_BANNER_START_PAGE);
                int tempNum = VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE;
                if (!ismArticlesEmpty()){
                    tempNum = mArticles.size();
                }
                mIndex = Math.abs(position - VALUE_BANNER_START_PAGE) % tempNum;

                if (position < VALUE_BANNER_START_PAGE && mIndex != 0){
                    mIndex = tempNum - mIndex;
                }
                changePoints(mIndex);
            }
        });

        return view;
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
        if (ismArticlesEmpty()){
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

//    class TimerRunner implements Runnable{
//        @Override
//        public void run() {
//            int currItem = mViewPager.getCurrentItem();
//            mViewPager.setCurrentItem(currItem+1);
//            if (mHandler != null){
//                mHandler.postDelayed(this, 5000);
//            }
//        }
//    }

    /**
     * 功能：若 mArticles 无意义，则 ismArticlesEmpty 为真
     * @return 若 mArticles 无意义，则 ismArticlesEmpty 为真
     */
    private boolean ismArticlesEmpty(){
        return (mArticles == null || mArticles.isEmpty());
    }

    /**
     * 功能：停止获取数据的定时器
     */
    private void stopScheduleRunner(){
        if (mScheduledExecutorService != null){
            mScheduledExecutorService.shutdown();
            mScheduledExecutorService = null;
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        mHandler.removeMessages(0);
//        mHandler = null;
//    }
}
