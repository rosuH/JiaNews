package me.rosuh.jianews.view;

import java.util.List;

import me.rosuh.jianews.bean.ArticleBean;

/**
 * @author rosu
 * @date 2018/9/30
 */
public interface IView {
    void onHeaderRequestFinished(List<ArticleBean> list);

    void onUpdateDataFinished(List<ArticleBean> list, int nextPos);

    void onUpdateDataFailed(Throwable t);

    void scrollToTop();
}
