package me.rosuh.jianews.util;


/**
 * 常量类
 *
 * @author rosu
 */
public class Const {
    /**
     * The constant TAG_FRAGMENT_BANNER.
     */
    public static final String TAG_FRAGMENT_BANNER = "tagFragmentBanner";

    /**
     * 请求文章列表父布局 ViewPager 刷新
     */
    public static final int REQUEST_CODE_ARTICLE_LIST_REFRESH = 1;

    /**
     * Home Activity pages value
     * Banner
     */
    public static final int VALUE_BANNER_MAX_PAGES = 1000;
    /**
     * The constant VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE.
     */
    public static final int VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE = 10;
    /**
     * The constant VALUE_BANNER_START_PAGE.
     */
    public static final int VALUE_BANNER_START_PAGE = VALUE_BANNER_MAX_PAGES / 2;
    /**
     * ArticleBean View Pager
     */
    public static final int VALUE_ARTICLE_MAX_PAGES = 4;
    /**
     * The constant VALUE_ARTICLE_START_PAGE.
     */
    public static final int VALUE_ARTICLE_START_PAGE = 0;
    /**
     * The constant VALUE_ARTICLE_INDEX_START.
     */
    public static final int VALUE_ARTICLE_INDEX_START = 0;
    /**
     * The constant VALUE_LIST_EMPTY_TYPE.
     */
    public static final int VALUE_LIST_EMPTY_TYPE = 1;
    /**
     * The constant VALUE_LIST_FOO_TYPE.
     */
    public static final int VALUE_LIST_FOO_TYPE = 2;
    /**
     * The constant VALUE_LIST_DEFAULT_TYPE.
     */
    public static final int VALUE_LIST_DEFAULT_TYPE = 4;
    /**
     * The constant VALUE_LIST_DEFAULT_SIZE.
     */
    public static final int VALUE_LIST_DEFAULT_SIZE = 10;
    /**
     * Fragment arguments key
     */
    public static final String KEY_ARGS_BANNER_ARTICLE = "argsBannerArticles";
    /**
     * The constant KEY_ARGS_ARTICLES_PAGE_URL.
     */
    public static final String KEY_ARGS_ARTICLES_PAGE_URL = "argsArticlesPosition";

    /**
     * Activity Intent key
     */
    public static final String KEY_INTENT_ARTICLE_READING_ITEM = "argsArticleReadingItem";
    /**
     * Web Spider
     */
    public static final String URL_MAJOR_NEWS = "http://www.jyu.edu.cn/index/zhyw1";
    public static final String URL_CAMPUS_ANNOUNCEMENT = "http://www.jyu.edu.cn/index/xygg1";
    public static final String URL_CAMPUS_ACTIVITIES = "http://www.jyu.edu.cn/index/xydt1";
    public static final String URL_MEDIA_REPORTS = "http://www.jyu.edu.cn/index/mtjy1";
    public static final String URL_HOME_PAGE = "http://www.jyu.edu.cn";

    public enum PageURL {
        /**
         * 综合要闻
         */
        URL_MAJOR_NEWS,
        /**
         * 校园公告
         */
        URL_CAMPUS_ANNOUNCEMENT,
        /**
         * 校园动态
         */
        URL_CAMPUS_ACTIVITIES,
        /**
         * 媒体报道
         */
        URL_MEDIA_REPORTS
    }

    public static PageURL getCorrectURL(int pos) {
        switch (pos) {
            case 1:
                return PageURL.URL_CAMPUS_ANNOUNCEMENT;
            case 2:
                return PageURL.URL_CAMPUS_ACTIVITIES;
            case 3:
                return PageURL.URL_MEDIA_REPORTS;
            case 0:
            default:
                return PageURL.URL_MAJOR_NEWS;
        }
    }
}
