package me.rosuh.jianews.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.rosuh.jianews.storage.ArticleDbSchema.ArticleTable;

/**
 * @author rosu
 * @date 2018/10/7
 */
public class ArticleBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "articleBase.db";

    public ArticleBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ArticleTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ArticleTable.Cols.ID + ", " +
                ArticleTable.Cols.URL + ", " +
                ArticleTable.Cols.TITLE + ", " +
                ArticleTable.Cols.DATE + ", " +
                ArticleTable.Cols.SUMMARY + ", " +
                ArticleTable.Cols.CONTENT + ", " +
                ArticleTable.Cols.THUMBNAIL +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
