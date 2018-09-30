package me.rosuh.android.jianews.storage;

import java.util.List;

import me.rosuh.android.jianews.bean.ArticleBean;

/**
 * @author rosu
 * @date 2018/9/30
 *
 * Presenter 需要实现的数据接口
 */
public interface IDataModel {
    void onInfo(int info);

    void onDataResponse(List<ArticleBean> articleBeanList);
}
