package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class ArticleListFragment extends Fragment {

    private RecyclerView mArticleRecyclerView;
    private ArticleAdapter mArticleAdapter;
    private List<Article> mArticles;
    private ScheduledExecutorService mScheduledExecutorService;
    private ArticleLab mArticleLab;
    private Context mContext;
    private String mRequestUrl;
    private static final String TAG = "ArticleListFragment";
    private RequestOptions mRequestOptions = new RequestOptions().centerCrop()
            .placeholder(R.drawable.logo_no).error(R.drawable.logo_no);


    public static Fragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(Const.KEY_ARGS_ARTICLES_POSITION, position);
        Fragment fragment = new ArticleListFragment();
        fragment.setArguments(args);
        return fragment;
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
        // 把 Viewpager 的 position 转换为相对应的文章类型页面链接
        int position = getArguments().getInt(Const.KEY_ARGS_ARTICLES_POSITION);
        switch (position){
            case 2:
                mRequestUrl = Const.URL_CAMPUS_ANNOUNCEMENT;
                break;
            case 3:
                mRequestUrl = Const.URL_CAMPUS_ACTIVITIES;
                break;
            case 4:
                mRequestUrl = Const.URL_MEDIA_REPORTS;
                break;
            case 1:
            default:
                mRequestUrl = Const.URL_MAJOR_NEWS;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_list_page_fragment, container, false);
        mArticleRecyclerView = view.findViewById(R.id.rv_article_list);
        final LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        mArticleRecyclerView.setLayoutManager(layout);
        mArticleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int pos = mArticleAdapter.getItemCount();
                int layoutPos = layout.findFirstVisibleItemPosition();
                /**
                 * @Test
                 */
                Log.d(TAG, "onScrollStateChanged: mArticleAdapter.getItemCount() = " + mArticleAdapter.getItemCount());
                Log.d(TAG, "onScrollStateChanged: layout.findFirstVisibleItemPosition() = " + layout.findFirstVisibleItemPosition());
                // End

                if (newState == SCROLL_STATE_IDLE){
                    if (layoutPos == 0){
                        // 滑动到 position 为 0 时，显示 header，并做好网络请求
                        mArticleAdapter.notifyItemInserted(0);
                        // 网络请求在这里做
                        getSpecificArticles(mRequestUrl);
                    }else if (layoutPos == mArticleAdapter.getItemCount() + 1){
                        mArticleAdapter.notifyItemInserted(mArticleAdapter.getItemCount() - 1);
                    }
                }
            }
        });
        updateUI();
        return view;
    }

    /**
     * 功能：根据数据创建适配器，将之设置给列表，后更新 UI
     */
    public void updateUI(){
        mArticleAdapter = new ArticleAdapter(mArticles);
        mArticleRecyclerView.setAdapter(mArticleAdapter);
        mArticleAdapter.notifyDataSetChanged();
        // 发送消息给 ArticleViewPagerFragment 告知更新 ViewPager
        if (getTargetFragment() != null && !ismArticlesEmpty()){
            getTargetFragment().onActivityResult(Const.REQUEST_CODE_ARTICLE_LIST_REFRESH
                    , Activity.RESULT_OK, new Intent());
        }
        Log.d(TAG, "updateUI: mArticleAdapter.notifyDataSetChanged() has been called.");
    }

    private class ArticleHolder extends RecyclerView.ViewHolder {
        private Article mArticle;
        private TextView mTitleTextView;
        private TextView mSummaryTextView;
        private TextView mPublishTimeTextView;
        private ImageView mThumbnailImageView;

        public ArticleHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.article_list_item_fragment, parent, false));
            mTitleTextView = itemView.findViewById(R.id.tv_article_title);
            mSummaryTextView = itemView.findViewById(R.id.tv_article_summary);
            mThumbnailImageView = itemView.findViewById(R.id.iv_article_thumbnail);
            mPublishTimeTextView = itemView.findViewById(R.id.tv_list_publish_time);
        }
        /**
         * 功能：被 Adapter 调用来绑定数据和视图
         *      1. 由 adapter 传入一个 article 对象
         *      2. 由本方法绑定
         * @param article 传入已填充数据的 article 对象
         */
        private void bind(Article article){
            this.mArticle = article;
            Glide.with(mContext).load(mArticle.getThumbnail()).apply(mRequestOptions).into(mThumbnailImageView);
            mTitleTextView.setText(mArticle.getTitle());
            mSummaryTextView.setText(mArticle.getSummary());
            mPublishTimeTextView.setText(mArticle.getPublishTime());
        }
    }

    /**
     * @author rosuh 2018-4-26 21:00:34
     * 功能：空视图 Holder 类，在真正的数据还没有获得前，加载本 holder
     *
     */
    private class EmptyHolder extends RecyclerView.ViewHolder {
        private Article mArticle;
        private TextView mTitleTextView;
        private TextView mSummaryTextView;
        private ImageView mThumbnailImageView;

        public EmptyHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.article_list_item_fragment, parent, false));
            mTitleTextView = itemView.findViewById(R.id.tv_article_title);
            mSummaryTextView = itemView.findViewById(R.id.tv_article_summary);
            mThumbnailImageView = itemView.findViewById(R.id.iv_article_thumbnail);

            Glide.with(mContext).load(R.drawable.logo_no).apply(mRequestOptions).into(mThumbnailImageView);
            mTitleTextView.setText(R.string.item_loading);
            mSummaryTextView.setText(R.string.item_loading);
        }
    }

    /**
     * @author rosuh 2018-4-28 21:43:21
     * 功能：下拉刷新 Holder 类。当用户下拉刷新的时候，加载本 holder
     *
     */
    private class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_header, parent, false));
        }

        public void refresh(){
            getSpecificArticles(mRequestUrl);
        }
    }

    /**
     * @author rosuh 2018-4-28 21:43:21
     * 功能：上拉加载 Holder 类。当用户上拉到底的时候，加载本 holder
     *
     */
    private class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_footer, parent, false));
        }
    }

    private class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Article> mArticles;

        public ArticleAdapter(List<Article> articles){
            mArticles = articles;
            if (mArticles == null){
                mArticles = new ArrayList<>();
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            switch (viewType){
                case Const.VALUE_LIST_EMPTY_TYPE:
                    return new EmptyHolder(layoutInflater, parent);
                case Const.VALUE_LIST_FOO_TYPE:
                    return new FooterHolder(layoutInflater, parent);
                case Const.VALUE_LIST_HEADER_TYPE:
                    return new HeaderHolder(layoutInflater, parent);
                default:
                    return new ArticleHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);

            if (type == Const.VALUE_LIST_DEFAULT_TYPE){
                Article article = mArticles.get(position);
                ArticleHolder viewHolder = (ArticleHolder)holder;
                viewHolder.bind(article);
            }else if (type == Const.VALUE_LIST_HEADER_TYPE){
                ((HeaderHolder) holder).refresh();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0){
                // 返回下拉刷新
                return Const.VALUE_LIST_HEADER_TYPE;
            }else if (position == getItemCount() - 1){
                // 返回上拉加载
                return Const.VALUE_LIST_FOO_TYPE;
            }
            if (mArticles.size() == 0){
                // 返回空视图
                return Const.VALUE_LIST_EMPTY_TYPE;
            }
            return Const.VALUE_LIST_DEFAULT_TYPE;
        }

        @Override
        public int getItemCount() {
            if (mArticles.isEmpty()){
                return 10;
            }
            return mArticles.size() + 1;
        }
    }

    /**
     * 功能：初始化文章列表
     *      1. 页面首次载入时调用，刷新或者获取下一页则由其他方法完成
     *      2. 获取成功后在线程内结束，抛出异常并捕获它
     * @param url  文章类型链接
     */
    private void getSpecificArticles(final String url){
        List<Article> list = mArticles;

        // 如果获取失败，则每个三秒重新获取一次，9 秒后失败则报告错误
        try {
            if (list == null || list.isEmpty()){
                mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        List<Article> temps = mArticleLab.getArticleList(url, Const.VALUE_ARTICLE_INDEX_START);
                        // 获取到数据则通知列表更新
                        if (temps != null && !temps.isEmpty()){
                            mArticles = temps;
                            Log.d(TAG, "run: temps.size() = " + temps.size());
                            stopRefresh();
                        }
                    }
                },0, 3000, TimeUnit.MILLISECONDS);
            }
        }catch (RuntimeException re){
            re.printStackTrace();
        }

    }

    /**
     * 功能：此方法在更新网络请求完成后被调用
     *      1. 删除下拉的 header
     *      2. 通知列表数据改变
     */
    private void stopRefresh(){
        mArticleAdapter.notifyItemRemoved(0);
        Toast.makeText(mContext, R.string.item_refresh_finished, Toast.LENGTH_SHORT).show();
        updateUI();
        stopScheduleRunner();
    }
    /**
     * 功能：停止获取数据的定时器
     */
    private void stopScheduleRunner(){
        Log.d(TAG, "stopScheduleRunner: has been executed.");
        if (mScheduledExecutorService != null){
            mScheduledExecutorService.shutdown();
            mScheduledExecutorService = null;
        }
    }

    /**
     * 功能：若 mArticles 无意义，则 ismArticlesEmpty 为真
     * @return 若 mArticles 无意义，则 ismArticlesEmpty 为真
     */
    private boolean ismArticlesEmpty(){
        return (mArticles == null || mArticles.isEmpty());
    }
}
