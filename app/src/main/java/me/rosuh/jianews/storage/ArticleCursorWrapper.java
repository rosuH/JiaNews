package me.rosuh.jianews.storage;

import android.database.Cursor;
import android.database.CursorWrapper;

import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.storage.ArticleDbSchema.ArticleTable.Cols;

/**
 * @author rosu
 * @date 2018/10/7
 */
public class ArticleCursorWrapper extends CursorWrapper {
    public ArticleCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ArticleBean getArticleBean(){
        int id = getInt(getColumnIndex(Cols.ID));
        String url = getString(getColumnIndex(Cols.URL));
        String title = getString(getColumnIndex(Cols.TITLE));
        String summary = getString(getColumnIndex(Cols.SUMMARY));
        String thumbnail = getString(getColumnIndex(Cols.THUMBNAIL));
        String content = getString(getColumnIndex(Cols.CONTENT));
        String date = getString(getColumnIndex(Cols.DATE));

        ArticleBean articleBean = new ArticleBean();
        articleBean.setId(id);
        articleBean.setUrl(url);
        articleBean.setTitle(title);
        articleBean.setDate(date);
        articleBean.setSummary(summary);
        articleBean.setThumbnail(thumbnail);
        articleBean.setContent(content);

        return articleBean;
    }
}
