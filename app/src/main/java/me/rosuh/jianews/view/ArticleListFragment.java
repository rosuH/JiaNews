package me.rosuh.jianews.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import me.rosuh.jianews.ArticleReadingActivity;
import me.rosuh.android.jianews.R;
import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.precenter.ArticleListViewPresenter;
import me.rosuh.jianews.util.Const;

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
public class ArticleListFragment extends Fragment implements IView {
    private static final String TAG = "ArticleListFragment";
    private RecyclerView mArticleRecyclerView;
    private ArticleAdapter mArticleAdapter;
    private List<ArticleBean> mArticleBeans;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private String mRequestUrl = Const.URL_MAJOR_NEWS;
    private Toast mToast;
    private RequestOptions mRequestOptions = new RequestOptions().centerCrop().fallback(R.drawable.logo_no)
            .placeholder(R.drawable.logo_no).error(R.drawable.logo_no);
    private ArticleListViewPresenter mArticleListViewPresenter = ArticleListViewPresenter.getInstance();


    /**
     * @param position TabLayout 选中的 item 位置
     * @return 附带有 position 的 fragment 实例
     */
    public static Fragment getInstances(int position) {
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
        int position = 0;
        if (getArguments() != null) {
            position = getArguments().getInt(Const.KEY_ARGS_ARTICLES_POSITION, 0);
        }
        mRequestUrl = getCorrectUrl(position);
        // 执行数据获取工作
        loadHeaderData();
    }

    /**
     * 从传入的 Viewpager 的 position 获取对应的链接
     *
     * @param pos ViewPager 的索引值
     * @return 正确的链接
     */
    private String getCorrectUrl(int pos) {
        switch (pos) {
            case 1:
                return Const.URL_CAMPUS_ANNOUNCEMENT;
            case 2:
                return Const.URL_CAMPUS_ACTIVITIES;
            case 3:
                return Const.URL_MEDIA_REPORTS;
            case 0:
            default:
                return Const.URL_MAJOR_NEWS;
        }
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
                if (lastType == Const.VALUE_LIST_FOO_TYPE) {
                    loadMoreData(layoutPos);
                }
            }
        });
        // 下拉刷新布局
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHeaderData();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        updateUI();
        return view;
    }

    /**
     * 获取首部数据
     */
    private void loadHeaderData(){
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mArticleListViewPresenter.requestHeaderData(ArticleListFragment.this, Const.VALUE_ARTICLE_INDEX_START, mRequestUrl);
            }
        });
    }

    /**
     * @param position 传入 layout.findLastCompletelyVisibleItemPosition() 作为当前可是列表最后项
     */
    private void loadMoreData(final int position) {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mArticleListViewPresenter.requestMoreData(ArticleListFragment.this, position, mRequestUrl);
            }
        });
    }

    /**
     * 功能：根据数据创建适配器，将之设置给列表，后更新 UI
     */
    public void updateUI() {
        mArticleAdapter = new ArticleAdapter(mArticleBeans);
        mArticleRecyclerView.setAdapter(mArticleAdapter);
        mArticleAdapter.notifyDataSetChanged();
        // 发送消息给 ArticleViewPagerFragment 告知更新 ViewPager
        if (getTargetFragment() != null && !isListEmpty(mArticleBeans)) {
            getTargetFragment().onActivityResult(Const.REQUEST_CODE_ARTICLE_LIST_REFRESH
                    , Activity.RESULT_OK, new Intent());
        }
        if (mSwipeRefreshLayout.isRefreshing() && mArticleBeans != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 文章类 Holder
     */
    private class ArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ArticleBean mArticleBean;
        private TextView mTitleTextView;
        private TextView mSummaryTextView;
        private TextView mPublishTimeTextView;
        private ImageView mThumbnailImageView;

        private ArticleHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.article_list_item_fragment, parent, false));
            mTitleTextView = itemView.findViewById(R.id.tv_article_title);
            mSummaryTextView = itemView.findViewById(R.id.tv_article_summary);
            mThumbnailImageView = itemView.findViewById(R.id.iv_article_thumbnail);
            mPublishTimeTextView = itemView.findViewById(R.id.tv_list_publish_time);
            itemView.setOnClickListener(this);
        }

        /**
         * 功能：被 Adapter 调用来绑定数据和视图
         * 1. 由 adapter 传入一个 articleBean 对象
         * 2. 由本方法绑定
         *
         * @param articleBean 传入已填充数据的 articleBean 对象
         */
        private void bind(ArticleBean articleBean) {
            this.mArticleBean = articleBean;
            mTitleTextView.setText(mArticleBean.getTitle());
            if (mArticleBean.getThumbnail() != null) {
                Glide.with(mContext).load(mArticleBean.getThumbnail()).apply(mRequestOptions)
                        .into(mThumbnailImageView);
            }
            if (mArticleBean.getContent() != null) {
                mSummaryTextView.setText(mArticleBean.getSummary());
            }
            mPublishTimeTextView.setText(mArticleBean.getDate());
        }

        @Override
        public void onClick(View v) {
            if (mArticleBean.getContent() == null && mArticleBean.getThumbnail() == null) {
                // 实现点击启动特定 article 的阅读页面
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mArticleBean.getUrl()));
                startActivity(intent);
            } else {
                startActivity(ArticleReadingActivity.newIntent(mArticleBean, getActivity()));
            }
        }
    }

    /**
     * @author rosuh 2018-4-26 21:00:34
     * 功能：空视图 Holder 类，在真正的数据还没有获得前，加载本 holder
     */
    private class EmptyHolder extends RecyclerView.ViewHolder {
        private ArticleBean mArticleBean;
        private TextView mTitleTextView;
        private TextView mSummaryTextView;
        private ImageView mThumbnailImageView;

        /**
         * Instantiates a new Empty holder.
         *
         * @param inflater the inflater
         * @param parent   the parent
         */
        public EmptyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.article_list_item_fragment, parent, false));
            mTitleTextView = itemView.findViewById(R.id.tv_article_title);
            mSummaryTextView = itemView.findViewById(R.id.tv_article_summary);
            mThumbnailImageView = itemView.findViewById(R.id.iv_article_thumbnail);

            Glide.with(mContext).load(R.drawable.logo_no).apply(mRequestOptions).into(mThumbnailImageView);
            mTitleTextView.setText(R.string.item_loading);
            mSummaryTextView.setText("...");
        }
    }

    /**
     * @author rosuh 2018-4-28 21:43:21
     * 功能：上拉加载 Holder 类。当用户上拉到底的时候，加载本 holder
     */
    private class FooterHolder extends RecyclerView.ViewHolder {
        /**
         * Instantiates a new Footer holder.
         *
         * @param inflater the inflater
         * @param parent   the parent
         */
        public FooterHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_footer, parent, false));
        }
    }

    private class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ArticleBean> mArticleBeans;
        private ArticleBean mArticleBean;

        private ArticleAdapter(List<ArticleBean> articleBeans) {
            mArticleBeans = articleBeans;
            if (mArticleBeans == null) {
                mArticleBeans = new ArrayList<>();
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            switch (viewType) {
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

            if (type == Const.VALUE_LIST_DEFAULT_TYPE) {
                mArticleBean = mArticleBeans.get(position);
                ArticleHolder viewHolder = (ArticleHolder) holder;
                viewHolder.bind(mArticleBean);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                // 返回上拉加载
                return Const.VALUE_LIST_FOO_TYPE;
            }
            if (mArticleBeans.size() == 0) {
                // 返回空视图
                return Const.VALUE_LIST_EMPTY_TYPE;
            }
            return Const.VALUE_LIST_DEFAULT_TYPE;
        }

        @Override
        public int getItemCount() {
            if (isListEmpty(mArticleBeans)) {
                return Const.VALUE_LIST_DEFAULT_SIZE;
            }
            return mArticleBeans.size();
        }

        private void addItems(List<ArticleBean> articleBeans) {
            mArticleBeans.addAll(articleBeans);
        }
    }

    @Override
    public void onStartRequest(List<ArticleBean> list) {
        stopRefresh(list);
    }

    @Override
    public void onUpdateDataFinished(List<ArticleBean> list, int nextPos) {
        stopLoading(list, nextPos);
    }

    @Override
    public void onUpdateDataFailed(Throwable t) {
        t.printStackTrace();
        Toast.makeText(mContext, "更新数据失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollToTop() {
        mArticleRecyclerView.smoothScrollToPosition(0);
    }

    /**
     * 功能：此方法在更新网络请求完成后被调用
     * 1. 暂停下拉刷新动画
     * 2. 通知列表数据改变
     */
    private void stopRefresh(List<ArticleBean> list) {
        if (isListEmpty(list)) {
            return;
        }
        if (isListEmpty(mArticleBeans)) {
            // 如果为空，则直接赋值
            mArticleBeans = list;
        } else if (isNewData(mArticleBeans, list)) {
            // 先获取增加的数据，然后把新数据复制到 mArticleBeans 头部
            int index = list.indexOf(mArticleBeans.get(0));
            List<ArticleBean> tmpList = list.subList(0, index);
            mArticleBeans.addAll(0, tmpList);
        }
        showToast(R.string.item_refresh_finished);
        updateUI();
    }

    private void stopLoading(List<ArticleBean> list, int nextPos) {
        if (isListEmpty(list)) {
            return;
        }
        int preSize = mArticleBeans.size();
        mArticleAdapter.addItems(list);
        mArticleAdapter.notifyItemRangeInserted(preSize, mArticleBeans.size());
        mArticleRecyclerView.scrollToPosition(nextPos);
    }

    /**
     * 功能：比较两个列表是否一致，以确认是否有新数据
     *
     * @param ori 原始列表
     * @param des 新的列表
     * @return 如果有新数据，返回 true，没有则返回 false
     */
    private boolean isNewData(List<ArticleBean> ori, List<ArticleBean> des) {
        return !ori.get(0).getId().equals(des.get(0).getId());
    }

    /**
     * 功能：若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     *
     * @return 若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     */
    private boolean isListEmpty(List list) {
        return (list == null || list.isEmpty());
    }


    /**
     * 显示 Toast
     *
     * @param resId 资源 id
     */
    private void showToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    /**
     * 取消 Toast
     */
    private void cancelToast() {
        if (mToast == null) {
            return;
        }
        mToast.cancel();
    }

}
