package me.rosuh.android.jianews.bean;

import android.text.TextUtils;

import java.util.List;

import me.rosuh.android.jianews.network.WebSpider;
import me.rosuh.android.jianews.util.Const;

/**
 * 这个类用于创建、获取 ArticleBean 及其集合的单例类
 * @author rosuh 2018-5-9
 * @version 0.1
 */

public class ArticleLab {
    private static final String TAG = "ArticleLab";

    private static class ArticleLabHolder{
        private static final ArticleLab INSTANCE = new ArticleLab();
    }

    public static ArticleLab getInstance(){
        return ArticleLabHolder.INSTANCE;
    }

    private ArticleLab(){
    }

    /**
     * 功能：根据传入的链接，调用 WebSpider 获取数据
     *      1. 直接返回获取的文章数据
     * @param url   文章类型链接
     * @return 返回获取的文章数据
     */
    public List<ArticleBean> getArticleList(String url, int index){
        if (TextUtils.isEmpty(url)){
            return null;
        }

        return WebSpider.getArticlesList(url, index);
    }
}
