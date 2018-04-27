package me.rosuh.android.jianews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.rosuh.android.jianews.Const.URL_CAMPUS_ACTIVITIES;
import static me.rosuh.android.jianews.Const.URL_CAMPUS_ANNOUNCEMENT;
import static me.rosuh.android.jianews.Const.URL_MAJOR_NEWS;
import static me.rosuh.android.jianews.Const.URL_MEDIA_REPORTS;

public class WebSpider {
    private static List<Article> sArticles;
    private static List<Article> sBannerArticles;


    public static List<Article> getArticlesList(String url, int index){
        /**
         * 链接判断和页码判断
         */
        boolean isUrlPointless = !url.equals(URL_MAJOR_NEWS) &&
                !url.equals(URL_CAMPUS_ACTIVITIES) &&
                !url.equals(URL_MEDIA_REPORTS) &&
                !url.equals(URL_CAMPUS_ANNOUNCEMENT);

        if ( isUrlPointless||index <= 0) {
            return null;
        }

        String mUrl = url + "_" + index + ".html";
        try {
            Document doc = Jsoup.connect(mUrl).get();
            Element uls = doc.getElementsByTag("ul").get(0);
            Elements links = uls.getElementsByTag("a");
            sArticles = dataFilterForList(links);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        return sArticles;
    }

    /**
     * 功能：传入已获取的文章链接集合，在此方法内执行数据获取和对 sArticleList 的数据填充
     *      1. id 为链接的尾 5 位数字
     *      2. title 直接获取
     *      3. url 直接获取
     *      4. content
     * @param links 文章链接集合
     * @return articles 返回已经填充好的文章列表
     */
    private static List<Article> dataFilterForList(Elements links){
        List<Article> articles = new ArrayList<>();
        try {
            for (Element link: links){
                Article article = new Article();
                article.setUrl(link.attr("href"));
                article.setTitle(link.text());
                // 获取文章内容
                String content = Jsoup.connect(article.getUrl()).get()
                        .select("[height=297]").get(0)
                        .getAllElements()
                        .toString();
                article.setContent(content);
                /**
                 * 获取第一张图片链接作为缩略图
                 * 如果为空，则获取，防止抛出异常
                 */
                Elements imgLinks = Jsoup.parse(content).getElementsByTag("img");
                if (!imgLinks.isEmpty()){
                    article.setThumbnail(imgLinks.get(0).attr("src"));
                }
                article.setId(article.getUrl());

                articles.add(article);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return articles;
    }

    public static List<Article> getBannerList(){
        try {
            Elements elements = Jsoup.connect(Const.URL_HOME_PAGE)
                    .get()
                    .select("td#demo1").get(0)
                    .select("div.best-pic");
            sBannerArticles = dataFilterForBanner(elements);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return sBannerArticles;
    }

    private static List<Article> dataFilterForBanner(Elements linkTags){
        List<Article> articles = new ArrayList<>();
        try {
            for (Element linkTag: linkTags){
                Article article = new Article();
                article.setTitle(linkTag.getElementsByTag("img").get(0).attr("alt"));
                article.setThumbnail(linkTag.getElementsByTag("img").get(0).attr("abs:src"));
                article.setUrl(linkTag.getElementsByTag("a").get(0).attr("href"));
                String content = Jsoup.connect(article.getUrl()).get()
                        .select("[height=297]").get(0)
                        .getAllElements()
                        .toString();
                article.setContent(content);
                // 暂时使用 URL 作为 id
                article.setId(article.getUrl());
                articles.add(article);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return articles;
    }

//    public static void printfAll(){
//        for (int i = 0; i < mLinks.size(); i++){
//            System.out.println(
//                    "Link[ " + i + " ] = " + mLinks.get(i) + " \n" +
//                            "Title[ " + i + " ] = " + mTitles.get(i) + " \n"
//            );
//        }
//    }
}
