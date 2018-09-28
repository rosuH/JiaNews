package me.rosuh.android.jianews.util;


/**
 * 常量类
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
    public static final int VALUE_BANNER_START_PAGE = VALUE_BANNER_MAX_PAGES/2;
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
     * The constant KEY_ARGS_ARTICLES_POSITION.
     */
    public static final String KEY_ARGS_ARTICLES_POSITION = "argsArticlesPosition";

    /**
     * Activity Intent key
     */
    public static final String KEY_INTENT_ARTICLE_READING_ITEM = "argsArticleReadingItem";
    /**
     * Web Spider
     */
    public static final String URL_MAJOR_NEWS = "http://www.jyu.edu.cn/news/index_2";
    /**
     * The constant URL_CAMPUS_ANNOUNCEMENT.
     */
    public static final String URL_CAMPUS_ANNOUNCEMENT = "http://www.jyu.edu.cn/news/index_3";
    /**
     * The constant URL_CAMPUS_ACTIVITIES.
     */
    public static final String URL_CAMPUS_ACTIVITIES = "http://www.jyu.edu.cn/news/index_44";
    /**
     * The constant URL_MEDIA_REPORTS.
     */
    public static final String URL_MEDIA_REPORTS = "http://www.jyu.edu.cn/news/index_52";
    /**
     * The constant URL_HOME_PAGE.
     */
    public static final String URL_HOME_PAGE = "http://www.jyu.edu.cn";

}
