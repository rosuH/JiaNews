package me.rosuh.android.jianews;

public class Const {
    public static final String TAG_FRAGMENT_BANNER = "tagFragmentBanner";
    public static final String TAG_FRAGMENT_ARTICLE_LIST = "tagFragmentArticleList";
    public static final String TAG_FRAGMENT_TAB_NAV = "tagFragmentTagNav";

    /**
     * Home Activity pages value
     * Banner
     */
    public static final int VALUE_BANNER_MAX_PAGES = 1000;
    public static final int VALUE_BANNER_DEFAULT_ACTUAL_PAGES_SIZE = 10;
    public static final int VALUE_BANNER_START_PAGE = VALUE_BANNER_MAX_PAGES/2;
    public static final int VALUE_BANNER_CACHE_PAGES = 3;

    /**
     * Article View Pager
     */
    public static final int VALUE_ARTICLE_MAX_PAGES = 4;
    public static final int VALUE_ARTICLE_START_PAGE = 1;
    public static final int VALUE_ARTICLE_INDEX_START = 1;
    public static final int VALUE_ARTICLE_SUMMARY = 40;
    public static final int VALUE_LIST_EMPTY_TYPE = 1;
    public static final int VALUE_LIST_FOO_TYPE = 2;
    public static final int VALUE_LIST_HEADER_TYPE = 3;

    /**
     * Tab Layout
     */
    public static final int VALUE_TAB_INDEX_MAIN = 1;
    public static final int VALUE_TAB_INDEX_ANNOUNCE = 2;
    public static final int VALUE_TAB_INDEX_ACTIVITY = 3;
    public static final int VALUE_TAB_INDEX_MEDIA = 4;

    /**
     * Fragment arguments key
     */
    public static final String KEY_ARGS_BANNER_POSITION = "argsBannerPosition";
    public static final String KEY_ARGS_BANNER_ARTICLE = "argsBannerArticles";
    public static final String KEY_ARGS_BANNER_ARTICLES_LIST = "argsBannerArticlesList";
    public static final String KEY_ARGS_ARTICLES_LIST = "argsArticlesList";
    public static final String KEY_ARGS_ARTICLES_POSITION = "argsArticlesPosition";

    /**
     * Web Spider
     */
    public static final String URL_MAJOR_NEWS = "http://www.jyu.edu.cn/news/index_2";
    public static final String URL_CAMPUS_ANNOUNCEMENT = "http://www.jyu.edu.cn/news/index_3";
    public static final String URL_CAMPUS_ACTIVITIES = "http://www.jyu.edu.cn/news/index_44";
    public static final String URL_MEDIA_REPORTS = "http://www.jyu.edu.cn/news/index_52";
    public static final String URL_HOME_PAGE = "http://www.jyu.edu.cn";

}
