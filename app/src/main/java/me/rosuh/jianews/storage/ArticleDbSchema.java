package me.rosuh.jianews.storage;

/**
 * @author rosu
 * @date 2018/10/7
 */
public class ArticleDbSchema {
    public static final class ArticleTable{
        public static final String NAME = "articles";

        public static final class Cols{
            public static final String ID = "id";
            public static final String URL = "url";
            public static final String TITLE = "title";
            public static final String SUMMARY = "summary";
            public static final String THUMBNAIL = "thumbnail";
            public static final String CONTENT = "content";
            public static final String DATE = "date";
        }
    }

}
