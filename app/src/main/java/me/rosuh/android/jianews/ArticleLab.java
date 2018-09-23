package me.rosuh.android.jianews;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;

/**
 * 这个类用于创建、获取 ArticleBean 及其集合的单例类
 * @author rosuh 2018-5-9
 * @version 0.1
 */

public class ArticleLab {
    private static final String TAG = "ArticleLab";
    private static ArticleLab sArticleLab;

    public static ArticleLab get(Context context){
        if (sArticleLab == null){
            sArticleLab = new ArticleLab(context);
        }
        return sArticleLab;
    }

    private ArticleLab(Context context){
    }

    /**
     * 功能：根据传入的链接，调用 WebSpider 获取数据
     *      1. 直接返回获取的文章数据
     * @param url   文章类型链接
     * @return 返回获取的文章数据
     */
    public List<ArticleBean> getArticleList(String url, int index){
        List<ArticleBean> articleBeans;
        if (TextUtils.isEmpty(url)){
            return null;
        }
        switch (url){
            case Const.URL_HOME_PAGE:
                articleBeans = WebSpider.getBannerList();
                break;
            default:
                articleBeans = WebSpider.getArticlesList(url, index);
        }

        return articleBeans;
    }
}
