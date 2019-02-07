package me.rosuh.jianews.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.storage.ArticleDbSchema.ArticleTable;
import me.rosuh.jianews.storage.ArticleDbSchema.ArticleTable.Cols;

/**
 * @author rosu
 * @date 2018/10/7
 */
public class LocalStorageManager {
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    public LocalStorageManager(Context context) {
        mContext = context;
        mSQLiteDatabase = new ArticleBaseHelper(mContext).getWritableDatabase();
    }

    public void addArticle(ArticleBean articleBean){
        if (articleBean == null){
            return;
        }
        ContentValues values = getContentValues(articleBean);

        mSQLiteDatabase.insert(ArticleTable.NAME, null, values);

    }

    public void updateArticle(ArticleBean articleBean){
        if (articleBean == null){
            return;
        }
        int id = articleBean.getId();
        ContentValues values = getContentValues(articleBean);
        mSQLiteDatabase.update(ArticleTable.NAME, values,
                Cols.ID + " = ?", new String[]{String.valueOf(id)});

    }

    public ArticleCursorWrapper queryArticles(String whereClause, String[] whereArgs){
        Cursor cursor = mSQLiteDatabase.query(
                ArticleTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new ArticleCursorWrapper(cursor);
    }

    /***
     * TODO 限制获取条目的数量，或根据传入的数值来获取
     */

    private List<ArticleBean> getArticles(){
        List<ArticleBean> list = new ArrayList<>();

        ArticleCursorWrapper cursorWrapper = queryArticles(null, null);

        cursorWrapper.moveToFirst();
        while (!cursorWrapper.isAfterLast()){
            list.add(cursorWrapper.getArticleBean());
            cursorWrapper.moveToNext();
        }
        cursorWrapper.close();
        return list;
    }

    private static ContentValues getContentValues(ArticleBean articleBean){
        ContentValues values = new ContentValues();
        values.put(Cols.ID, articleBean.getId());
        values.put(Cols.URL, articleBean.getUrl());
        values.put(Cols.TITLE, articleBean.getTitle());
        values.put(Cols.DATE, articleBean.getDate());
        values.put(Cols.SUMMARY, articleBean.getSummary());
        values.put(Cols.THUMBNAIL, articleBean.getThumbnail());
        values.put(Cols.CONTENT, articleBean.getContent());

        return values;
    }
}
