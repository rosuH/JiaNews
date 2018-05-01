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
     *      1. 如果静态变量有意义，则返回正确类型的文章列表
     *      2. 如果没有意义，则获取数据
     * @param url   文章类型链接
     * @return 如果本地静态变量有意义，直接返回；否则返回 null
     */
    public List<Article> getArticleList(String url, int index){
        if (url == null){
            Log.i(TAG, "The url is null");
            return null;
        }
        if (index < 0){
            index = 0;
        }

        switch (url){
            case Const.URL_MAJOR_NEWS:
                if (sMainNewsArticles.isEmpty()){
                    sMainNewsArticles = getDataBackground(url, index);
                }
                return sMainNewsArticles;
            case Const.URL_CAMPUS_ACTIVITIES:
                if (sActivityArticles.isEmpty()){
                    sActivityArticles = getDataBackground(url, index);
                }
                return sActivityArticles;
            case Const.URL_CAMPUS_ANNOUNCEMENT:
                if (sAnnouncementArticles.isEmpty()){
                    sAnnouncementArticles = getDataBackground(url, index);
                }
                return sAnnouncementArticles;
            case Const.URL_MEDIA_REPORTS:
                if (sMediaArticles.isEmpty()){
                    sMediaArticles = getDataBackground(url, index);
                }
                return sMediaArticles;
            default:
        }
        return null;
    }

    /**
     * 功能：获取轮播图文章
     *      1. 如果静态变量有意义，则返回正确类型的文章列表
     *      2. 如果没有意义，则获取数据
     * @return
     */
    public List<Article> getBannerArticles(){
        if (sBannerArticles.isEmpty()){
            sBannerArticles = getDataBackground(Const.URL_HOME_PAGE, 0);
        }
        return sBannerArticles;
    }

    private List<Article> getDataBackground(String url, int index){
        List<Article> articles;

        if (url.equals(Const.URL_HOME_PAGE)){
            articles = WebSpider.getBannerList();
        }else {
            articles = WebSpider.getArticlesList(url, index);
        }
        if (articles == null){
            Log.i(TAG, "Articles List from WebSpider.getData is null");
        }
        return articles;
    }
}
