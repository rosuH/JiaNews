package me.rosuh.android.jianews;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ArticleLab {
    private static final String TAG = "ArticleLab";
    private static ArticleLab sArticleLab;
    /**
     * 文章列表静态变量：
     * 1. 综合要闻
     * 2. 校园公告
     * 3. 校园动态
     * 4. 媒体报道
      */
    private static List<Article> sMainNewsArticles = new ArrayList<>();
    private static List<Article> sAnnouncementArticles = new ArrayList<>();
    private static List<Article> sActivityArticles = new ArrayList<>();
    private static List<Article> sMediaArticles = new ArrayList<>();
    private static List<Article> sBannerArticles = new ArrayList<>();


    public static ArticleLab get(Context context){
        if (sArticleLab == null){
            sArticleLab = new ArticleLab(context);
        }
        return sArticleLab;
    }

    private ArticleLab(Context context){
    }

    /**
     * 功能：根据传入的链接，判断返回的文章类型
     *      1. 直接返回获取的文章数据
     *      2. index 的大小是为 10 的倍数，1 = 10， 2 = 20 的意思，我们在程序里为它人工页
     *          2.1 如果到了 50 条件记录，也就是 index == 5 的时候，获取的网页链接会递增
     * @param url   文章类型链接
     * @return 返回获取的文章数据
     */
    public List<Article> getArticleList(String url, int index){
        List<Article> articles;
        if (url == null){
            Log.i(TAG, "The url is null");
            return null;
        }
        switch (url){
            case Const.URL_HOME_PAGE:
                articles = WebSpider.getBannerList();
                break;
            default:
                articles = WebSpider.getArticlesList(url, index);
        }

        return articles;
    }
}
