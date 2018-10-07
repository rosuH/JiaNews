package me.rosuh.jianews.precenter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.bean.ArticleLab;
import me.rosuh.jianews.storage.IDataModel;
import me.rosuh.jianews.util.Const;
import me.rosuh.jianews.util.ViewUtils;
import me.rosuh.jianews.view.ArticleListFragment;
import me.rosuh.jianews.view.IView;

/**
 * @author rosu
 * @date 2018/9/30
 */
public class ArticleListViewPresenter implements IDataModel {
    private static class ArticleListViewPresenterHolder {
        private static final ArticleListViewPresenter INSTANCE = new ArticleListViewPresenter();
    }

    public static ArticleListViewPresenter getInstance() {
        return ArticleListViewPresenterHolder.INSTANCE;
    }

    private ArticleListViewPresenter(){}

    private Map<String, IView> mIViewMap = new ConcurrentHashMap<>();
    private ArticleLab mArticleLab;

    public void requestHeaderData(ArticleListFragment context, final int index, final String url){
        if (context == null){
            return;
        }
        if (mArticleLab == null){
            mArticleLab = ArticleLab.getInstance();
        }
        mIViewMap.put(url, context);
        // 获取到数据则通知列表更新
        if (index != Const.VALUE_ARTICLE_INDEX_START){
            return;
        }
        final List<ArticleBean> list = mArticleLab.getArticleList(url, index);
        if (ViewUtils.isInMainThread()){
            mIViewMap.get(url).onStartRequest(list);
        }else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    mIViewMap.get(url).onStartRequest(list);
                }
            });
        }

    }


    public void requestMoreData(ArticleListFragment context, final int index, final String url){
        if (context == null){
            return;
        }
        if (mArticleLab == null){
            mArticleLab = ArticleLab.getInstance();
        }
        mIViewMap.put(url, context);
        // 获取到数据则通知列表更新
        if (index == Const.VALUE_ARTICLE_INDEX_START){
            return;
        }

        final List<ArticleBean> list = mArticleLab.getArticleList(url, index);
        if (ViewUtils.isInMainThread()){
            mIViewMap.get(url).onUpdateDataFinished(list, index + 1);
        }else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    mIViewMap.get(url).onUpdateDataFinished(list, index + 1);
                }
            });
        }
    }

    @Override
    public void onInfo(int info) {

    }

    @Override
    public void onDataResponse(List<ArticleBean> articleBeanList) {

    }
}
