package me.rosuh.android.jianews;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ArticleListFragment extends Fragment {

    private RecyclerView mArticleRecyclerView;
    private ArticleAdapter mArticleAdapter;
    private List<Article> mArticles;
    private ScheduledExecutorService mScheduledExecutorService;
    private ArticleLab mArticleLab;

    public static Fragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(Const.KEY_ARGS_ARTICLES_POSITION, position);
        Fragment fragment = new ArticleListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getArguments().getInt(Const.KEY_ARGS_ARTICLES_POSITION);
        switch (position){
            case 2:
                getSpecificArticles(Const.URL_CAMPUS_ANNOUNCEMENT);
                break;
            case 3:
                getSpecificArticles(Const.URL_CAMPUS_ACTIVITIES);
                break;
            case 4:
                getSpecificArticles(Const.URL_MEDIA_REPORTS);
                break;
            case 1:
            default:
                getSpecificArticles(Const.URL_MAJOR_NEWS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_list_page_fragment, container, false);
        mArticleRecyclerView = view.findViewById(R.id.rv_article_list);
        mArticleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    /**
     * 功能：根据数据创建适配器，将之设置给列表，后更新 UI
     */
    public void updateUI(){
        mArticleAdapter = new ArticleAdapter(mArticles);
        mArticleRecyclerView.setAdapter(mArticleAdapter);
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
            Glide.with(getContext()).load(mArticle.getThumbnail()).into(mThumbnailImageView);
            mTitleTextView.setText(mArticle.getTitle());
            mSummaryTextView.setText(mArticle.getContent().subSequence(0, Const.VALUE_ARTICLE_SUMMARY));
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

            Glide.with(getContext()).load(R.drawable.logo_no).into(mThumbnailImageView);
            mTitleTextView.setText(R.string.item_loading);
            mSummaryTextView.setText(R.string.item_loading);
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
                    break;
                case Const.VALUE_LIST_HEADER_TYPE:
                    break;
                default:
                    return new ArticleHolder(layoutInflater, parent);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);

            if (type == Const.VALUE_LIST_EMPTY_TYPE){
                return;
            }else if (type == Const.VALUE_LIST_HEADER_TYPE){
                return;
            }else if (type == Const.VALUE_LIST_FOO_TYPE){
                return;
            }else {
                Article article = mArticles.get(position);
                ArticleHolder viewHolder = (ArticleHolder)holder;
                viewHolder.bind(article);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mArticles.size() == 0){
                // 返回空视图
                return Const.VALUE_LIST_EMPTY_TYPE;
            }else if (position + 1 == getItemCount()){
                // 返回上拉加载
                return Const.VALUE_LIST_FOO_TYPE;
            }else if (position == -1){
                // 返回下拉刷新
                return Const.VALUE_LIST_HEADER_TYPE;
            }
            return super.getItemViewType(position);
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
                mScheduledExecutorService = Executors.newScheduledThreadPool(1);
                mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        List<Article> temps = mArticleLab.getArticleList(url, Const.VALUE_ARTICLE_INDEX_START);
                        // 获取到数据则通知列表更新
                        if (temps != null && !temps.isEmpty()){
                            mArticles = temps;
                            updateUI();
                            stopScheduleRunner();
                        }
                    }
                },3000, 3000, TimeUnit.MILLISECONDS);
            }
        }catch (RuntimeException re){
            stopScheduleRunner();
            re.printStackTrace();
        }

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

    /**
     * 功能：若 mArticles 无意义，则 ismArticlesEmpty 为真
     * @return 若 mArticles 无意义，则 ismArticlesEmpty 为真
     */
    private boolean ismArticlesEmpty(){
        return (mArticles == null || mArticles.isEmpty());
    }
}
