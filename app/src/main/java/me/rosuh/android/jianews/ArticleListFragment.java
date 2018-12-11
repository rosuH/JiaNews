package me.rosuh.android.jianews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类是文章列表的 Fragment 类，在这里类中实现了：
 * 1. 对 RecyclerView 的初始化
 * - 当数据没有准备好时，加载 Empty 视图
 * - 当数据准备好时，刷新列表视图
 * 2. 下拉刷新
 * 3. 上拉加载视图
 * 4. 通过 item 的点击来启动对应的文章阅读页面
 *
 * @author rosuh 2018-5-9
 * @version 0.1
 */
public class ArticleListFragment extends Fragment {

    private RecyclerView mArticleRecyclerView;
    private ArticleAdapter mArticleAdapter;
    private List<Article> mArticles;
    private List<Article> mArticlesSync = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private String mRequestUrl;
    private GetDataTask mGetDataTask;
    private RequestOptions mRequestOptions = new RequestOptions().centerCrop().fallback(R.drawable.logo_no)
            .placeholder(R.drawable.logo_no).error(R.drawable.logo_no);


    /**
     * New instance fragment.
     *
     * @param position TabLayout 选中的 item 位置
     * @return 附带有 position 的 fragment 实例
     */
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
        // 把 Viewpager 的 position 转换为相对应的文章类型页面链接
        int position;
       if (getArguments() != null){
           position = getArguments().getInt(Const.KEY_ARGS_ARTICLES_POSITION, 1);
       }else {
           position = 1;
       }
        switch (position){
            case 1:
                mRequestUrl = Const.URL_CAMPUS_ANNOUNCEMENT;
                break;
            case 2:
                mRequestUrl = Const.URL_CAMPUS_ACTIVITIES;
                break;
            case 3:
                mRequestUrl = Const.URL_MEDIA_REPORTS;
                break;
            case 0:
            default:
                mRequestUrl = Const.URL_MAJOR_NEWS;
        }

        // 执行数据获取工作
        mGetDataTask = new GetDataTask(ArticleListFragment.this, Const.VALUE_ARTICLE_INDEX_START);
        mGetDataTask.execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_list_page_fragment, container, false);
        mArticleRecyclerView = view.findViewById(R.id.rv_article_list);
        mSwipeRefreshLayout = view.findViewById(R.id.srl_list);
        final LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        mArticleRecyclerView.setLayoutManager(layout);
        mArticleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int layoutPos = layout.findLastCompletelyVisibleItemPosition();
                int lastType = mArticleAdapter.getItemViewType(layoutPos);
                // 如果最后一个 item 的类型是 VALUE_LIST_FOO_TYPE，那么调用加载更多数据方法
                if (lastType == Const.VALUE_LIST_FOO_TYPE){
                    loadMoreData(layoutPos);
                }
            }
        });
        // 下拉刷新布局
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGetDataTask = new GetDataTask(ArticleListFragment.this, Const.VALUE_ARTICLE_INDEX_START);
                mGetDataTask.execute();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        updateUI();
        return view;
    }


    /**
     * @param position 传入 layout.findLastCompletelyVisibleItemPosition() 作为当前可是列表最后项
     */
    private void loadMoreData(int position){
        mGetDataTask = new GetDataTask(ArticleListFragment.this, position);
        mGetDataTask.execute();
    }

    /**
     * 功能：根据数据创建适配器，将之设置给列表，后更新 UI
     */
    public void updateUI(){
        mArticleAdapter = new ArticleAdapter(mArticles);
        mArticleRecyclerView.setAdapter(mArticleAdapter);
        mArticleAdapter.notifyDataSetChanged();
        // 发送消息给 ArticleViewPagerFragment 告知更新 ViewPager
        if (getTargetFragment() != null && !isListEmpty(mArticles)){
            getTargetFragment().onActivityResult(Const.REQUEST_CODE_ARTICLE_LIST_REFRESH
                    , Activity.RESULT_OK, new Intent());
        }
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 文章类 Holder
     */
    private class ArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Article mArticle;
        private TextView mTitleTextView;
        private TextView mSummaryTextView;
        private TextView mPublishTimeTextView;
        private ImageView mThumbnailImageView;

        private ArticleHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.article_list_item_fragment, parent, false));
            mTitleTextView = itemView.findViewById(R.id.tv_article_title);
            mSummaryTextView = itemView.findViewById(R.id.tv_article_summary);
            mThumbnailImageView = itemView.findViewById(R.id.iv_article_thumbnail);
            mPublishTimeTextView = itemView.findViewById(R.id.tv_list_publish_time);
            itemView.setOnClickListener(this);
        }
        /**
         * 功能：被 Adapter 调用来绑定数据和视图
         *      1. 由 adapter 传入一个 article 对象
         *      2. 由本方法绑定
         * @param article 传入已填充数据的 article 对象
         */
        private void bind(Article article){
            this.mArticle = article;
            mTitleTextView.setText(mArticle.getTitle());
            GlideApp.with(mContext)
                    .load(mArticle.getThumbnail())
                    .error(R.drawable.logo_no)
                    .apply(mRequestOptions)
                    .into(mThumbnailImageView);
            if (mArticle.getContent() != null){
                mSummaryTextView.setText(mArticle.getSummary());
            }
            mPublishTimeTextView.setText(mArticle.getPublishTime());
        }

        @Override
        public void onClick(View v) {
            if (mArticle.getContent() == null && mArticle.getThumbnail() == null){
                // 实现点击启动特定 article 的阅读页面
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mArticle.getUrl()));
                startActivity(intent);
            }else {
                startActivity(ArticleReadingActivity.newIntent(mArticle, getActivity()));
            }
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

        /**
         * Instantiates a new Empty holder.
         *
         * @param inflater the inflater
         * @param parent   the parent
         */
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
     * 功能：上拉加载 Holder 类。当用户上拉到底的时候，加载本 holder
     *
     */
    private class FooterHolder extends RecyclerView.ViewHolder {
        /**
         * Instantiates a new Footer holder.
         *
         * @param inflater the inflater
         * @param parent   the parent
         */
        public FooterHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_footer, parent, false));
        }
    }

    private class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Article> mArticles;
        private Article mArticle;

        private ArticleAdapter(List<Article> articles){
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
                default:
                    return new ArticleHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);

            if (type == Const.VALUE_LIST_DEFAULT_TYPE){
                mArticle = mArticles.get(position);
                ArticleHolder viewHolder = (ArticleHolder)holder;
                viewHolder.bind(mArticle);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1){
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
            if (isListEmpty(mArticles)){
                return Const.VALUE_LIST_DEFAULT_SIZE;
            }
            return mArticles.size();
        }

        private void addItems(List<Article> articles){
            mArticles.addAll(articles);
        }
    }

    /**
     * 功能： 通过链接和页码获取文章数据
     */
    private static class GetDataTask extends AsyncTask<Integer, Void, Void>{
        private WeakReference<ArticleListFragment> mWeakReference;
        private List<Article> list;
        private String mUrl;
        private int mIndex;
        private ArticleLab mArticleLab;

        /**
         * Instantiates a new Get data task.
         *
         * @param context the context
         * @param index   the index
         */
        GetDataTask(ArticleListFragment context, int index){
            this.mWeakReference = new WeakReference<>(context);
            this.list = mWeakReference.get().mArticles;
            this.mUrl = mWeakReference.get().mRequestUrl;
            this.mIndex = index;
        }

        @Override
        protected Void doInBackground(Integer...integers) {
            mArticleLab = ArticleLab.get(this.mWeakReference.get().mContext);
            List<Article> temps = mArticleLab.getArticleList(mUrl, mIndex);
            // 获取到数据则通知列表更新
            if (temps != null && !temps.isEmpty()){
                list = temps;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mWeakReference.get().mArticlesSync == null){
                return;
            }
            mWeakReference.get().mArticlesSync = list;
            if (mIndex == Const.VALUE_ARTICLE_INDEX_START){
                mWeakReference.get().stopRefresh();
            }else {
                mWeakReference.get().stopLoading();
            }
        }
    }

    /**
     * 功能：此方法在更新网络请求完成后被调用
     *      1. 暂停下拉刷新动画
     *      2. 通知列表数据改变
     */
    private void stopRefresh(){
        if (isListEmpty(mArticlesSync)){
            return;
        }
        if (isListEmpty(mArticles)){
            // 如果为空，则直接赋值
            mArticles = mArticlesSync;
        }else if (isNewData(mArticles, mArticlesSync)){
            // 先获取增加的数据，然后把新数据复制到 mArticles 头部
            int index = mArticlesSync.indexOf(mArticles.get(0));
            List<Article> list = mArticlesSync.subList(0, index);
            mArticles.addAll(0, list);
        }
        Toast.makeText(mContext, R.string.item_refresh_finished, Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void stopLoading(){
        if (isListEmpty(mArticlesSync)){
            return;
        }
        int size = mArticles.size();
        mArticleAdapter.addItems(mArticlesSync);
        mArticleAdapter.notifyItemRangeInserted(size, mArticles.size());
    }

    /**
     * 功能：比较两个列表是否一致，以确认是否有新数据
     * @param ori   原始列表
     * @param des   新的列表
     * @return  如果有新数据，返回 true，没有则返回 false
     */
    private boolean isNewData(List<Article> ori, List<Article> des){
        return !ori.get(0).getId().equals(des.get(0).getId());
    }

    /**
     * 功能：若 mArticles 无意义，则 ismArticlesEmpty 为真
     * @return 若 mArticles 无意义，则 ismArticlesEmpty 为真
     */
    private boolean isListEmpty(List list){
        return (list == null || list.isEmpty());
    }
}
