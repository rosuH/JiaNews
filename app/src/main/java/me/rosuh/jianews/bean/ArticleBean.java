package me.rosuh.jianews.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 这个类是 ArticleBean 文章类，描述了 ArticleBean 这个类的成员和方法
 * @author rosuh 2018-5-9
 * @version 0.1
 */
public class ArticleBean implements Parcelable{
    /**
     * 文章 ID
     * 文章链接
     * 文章标题
     * 文章概括
     * 文章缩略图
     * 文章内容
     * 文章发布时间
     * 文章点击数
     * 文章的类型
     * 是否已被阅读
     */
    private String id;
    private String url;
    private String title;
    private String summary;
    private String thumbnail;
    private String content;
    private String date;
    private String type;
    private boolean isRead;

    public ArticleBean(){ }

    protected ArticleBean(Parcel in){
        this.id = in.readString();
        this.url = in.readString();
        this.title = in.readString();
        this.thumbnail = in.readString();
        this.content = in.readString();
        this.date = in.readString();
        this.type = in.readString();
        this.isRead = in.readByte() != 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public static final Creator<ArticleBean> CREATOR = new Creator<ArticleBean>() {
        @Override
        public ArticleBean createFromParcel(Parcel source) {
            return new ArticleBean(source);
        }

        @Override
        public ArticleBean[] newArray(int size) {
            return new ArticleBean[size];
        }
    };

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeString(this.thumbnail);
        dest.writeString(this.content);
        dest.writeString(this.date);
        dest.writeSerializable(this.type);
        dest.writeByte((byte)(this.isRead ? 1 : 0));
    }
}
