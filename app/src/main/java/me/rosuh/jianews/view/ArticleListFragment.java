package me.rosuh.jianews.view;

import android.content.Context;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import me.rosuh.android.jianews.R;
import me.rosuh.jianews.adapter.ArticleAdapter;
import me.rosuh.jianews.adapter.ArticleAdapter.FooterHolder;
import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.precenter.ArticleListViewPresenter;
import me.rosuh.jianews.util.Const;
import me.rosuh.jianews.util.ResponseThrowable;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

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
 * @version 0.1l
 */
public class ArticleListFragment extends Fragment implements IView {

    private static final String TAG = "ArticleListFragment";

    private RecyclerView mArticleRecyclerView;

    private ArticleAdapter mArticleAdapter;

    private List<ArticleBean> mArticleBeans = new ArrayList<>(20);

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Context mContext;

    private Const.PageURL mRequestUrl = Const.PageURL.URL_MAJOR_NEWS;

    private Toast mToast;

    private LinearLayoutManager mLinearLayoutManager;

    private ArticleListViewPresenter mViewPresenter = ArticleListViewPresenter.INSTANCE;

    private AtomicBoolean mIsRequesting = new AtomicBoolean(false);


    /**
     * @param pageURL TabLayout 选中的 item 位置
     * @return 附带有 position 的 fragment 实例
     */
    public static Fragment getInstances(Const.PageURL pageURL) {
        Bundle args = new Bundle();
        args.putSerializable(Const.KEY_ARGS_ARTICLES_PAGE_URL, pageURL);
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
        if (getArguments() != null) {
            mRequestUrl = (Const.PageURL) getArguments().getSerializable(Const.KEY_ARGS_ARTICLES_PAGE_URL);
        }
        // 执行数据获取工作
        loadHeaderData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_list_page_fragment, container, false);
        mArticleRecyclerView = view.findViewById(R.id.rv_article_list);
        mSwipeRefreshLayout = view.findViewById(R.id.srl_list);
        mArticleRecyclerView.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(getActivity()));
        mArticleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItemPos = mLinearLayoutManager.findLastVisibleItemPosition();
                int firstVisibleItemPos = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastType = mArticleAdapter.getItemViewType(lastVisibleItemPos);
                // 如果最后一个 item 的类型是 VALUE_LIST_FOO_TYPE，那么调用加载更多数据方法
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        if (lastType == Const.VALUE_LIST_FOO_TYPE) {
                            loadMoreData(lastVisibleItemPos);
                        } else if (lastType == Const.VALUE_LIST_DEFAULT_TYPE && firstVisibleItemPos == 0) {
                            loadHeaderData();
                        } else if (mArticleBeans.isEmpty()) {
                            loadHeaderData();
                        }
                        break;
                    default:
                }
            }
        });
        // 下拉刷新布局
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(this::loadHeaderData);
        mSwipeRefreshLayout.setRefreshing(true);
        updateUI();
        return view;
    }

    /**
     * 异步获取数据
     */
    private void loadHeaderData() {
        if (mIsRequesting.compareAndSet(false, true)) {
            mViewPresenter.requestHeaderData(ArticleListFragment.this,
                    Const.VALUE_ARTICLE_INDEX_START, mRequestUrl);
        }
    }

    /**
     * @param position 传入 mLinearLayoutManager.findLastCompletelyVisibleItemPosition() 作为当前可是列表最后项
     */
    private void loadMoreData(final int position) {
        Log.i(TAG, "loadMoreData: position ==== " + position);
        if (mIsRequesting.compareAndSet(false, true)) {
            mViewPresenter
                    .requestMoreData(ArticleListFragment.this, position, mRequestUrl);
        }
    }

    /**
     * 功能：根据数据创建适配器，将之设置给列表，后更新 UI
     */
    private void updateUI() {
        mArticleAdapter = new ArticleAdapter(Objects.requireNonNull(this.getActivity()), mArticleBeans, this);
        mArticleRecyclerView.setAdapter(mArticleAdapter);
        mArticleAdapter.setFooterHolder(
                new FooterHolder(Objects.requireNonNull(getActivity()).getLayoutInflater(), mArticleRecyclerView)
        );
        mArticleAdapter.notifyItemInserted(mArticleBeans.size());
        stopSwipeRefresh();
    }

    private void stopSwipeRefresh(){
        if (mSwipeRefreshLayout.isRefreshing() && !mArticleBeans.isEmpty()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onHeaderRequestFinished(List<ArticleBean> list) {
        mIsRequesting.compareAndSet(true, false);
        stopRefresh(list);
    }

    @Override
    public void onUpdateDataFinished(List<ArticleBean> list, int nextPos) {
        mIsRequesting.compareAndSet(true, false);
        stopLoading(list);
    }

    @Override
    public void onUpdateDataFailed(Throwable t) {
        mIsRequesting.compareAndSet(true, false);
        t.printStackTrace();
        Toast.makeText(mContext, t.getMessage().concat("\n 请稍后重试"), Toast.LENGTH_LONG).show();
        if (t.getClass().equals(ResponseThrowable.class)){
            mViewPresenter.disposeAll();
        }
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
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
//        showToast(R.string.item_refresh_finished);
        updateUI();
    }

    private void stopLoading(List<ArticleBean> list) {
        int prePos = mArticleBeans.size();
        if (isListEmpty(list)) {
            // 没有更多数据
            showToast("没有更多文章啦~");
            mArticleAdapter.setFooterHolder(null);
            mArticleAdapter.notifyItemRemoved(mArticleAdapter.getItemCount() + 1);
            return;
        }
        mArticleAdapter.addItems(list);
        mArticleAdapter.notifyItemRangeInserted(prePos + 1, mArticleBeans.size());
    }

    /**
     * 功能：比较两个列表是否一致，以确认是否有新数据
     *
     * @param ori 原始列表
     * @param des 新的列表
     * @return 如果有新数据，返回 true，没有则返回 false
     */
    private boolean isNewData(List<ArticleBean> ori, List<ArticleBean> des) {
        return !(ori.get(0).getId() == des.get(0).getId());
    }

    /**
     * 功能：若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     *
     * @return 若 mArticleBeans 无意义，则 ismArticlesEmpty 为真
     */
    private boolean isListEmpty(List list) {
        return (list == null || list.isEmpty());
    }

    public void onItemClick(ArticleBean clickedBean){
        ((HomeActivity)getActivity()).onItemClick(clickedBean);
    }

    /**
     * 显示 Toast
     *
     * @param resId 资源 id
     */
    private void showToast(int resId) {
        showToast(getString(resId));
    }

    private void showToast(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
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
