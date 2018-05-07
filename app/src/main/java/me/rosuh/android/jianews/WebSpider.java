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
    /**
     * 功能：根据传入的 url 和 index，进行文章数据的获取
     *      1. index 有两种情况
     *          - index 为 0，这个时候是刷新或者初次载入列表的时候获取的，代表获取网页第一页，前 10 篇文章
     *          - index 不为零，此时是用来加载更多的时候传入的列表最后一个 item 的 position
     *      2. 如果到了 50 条件记录，也就是 index == 5 的时候，获取的网页链接索引会递增
     * @param url   传入的网页链接
     * @param index 传入的索引值
     * @return  获取的文章列表
     */
    public static List<Article> getArticlesList(String url, int index){
        /**
         * 链接判断和页码判断
         */
        boolean isUrlPointless = !url.equals(URL_MAJOR_NEWS) &&
                !url.equals(URL_CAMPUS_ACTIVITIES) &&
                !url.equals(URL_MEDIA_REPORTS) &&
                !url.equals(URL_CAMPUS_ANNOUNCEMENT);

        String mUrl;
        final int articlesCountPerPage = 50;
        final int articlesListPerQueue = 10;

        // 传入的 position 总是为 9、19、29，所以需要 +1 以便判断
        int count = (index+1) / articlesCountPerPage;


        if (isUrlPointless||index < 0) {
            return null;
        } else if (index == 0) {
            // 文章索引为 0，也就是刷新或者首次载入列表的时候
            // 文章页链接为 第一页链接，index 的值需要重设为 10，方便后面做循环
            mUrl = url + ".html";
            index = index + articlesListPerQueue;
        }else if (count == 0){
            // count 计算的是网页的页码，如果文章索引低于 50，那么就是在第一页之内
            mUrl = url + ".html";
            // 因为 index = 0 时，是为刷新的情况，所以 9 的时候，是为加载下一页的情况，为了方便循环，这里为之加上 10
            index = index + articlesListPerQueue + 1;
        }else {
            // 当文章索引值超过了 50，那么需要请求下一页，此时 index 需通过页码的倍数进行计算
            mUrl = url + "_" + count + ".html";
            index = index + articlesListPerQueue + 1 - articlesCountPerPage * count;
        }

        try {
            Document doc = Jsoup.connect(mUrl).get();
            Element uls = doc.getElementsByTag("ul").get(0);
            Elements links = uls.getElementsByTag("a");
            Elements dates = doc.getElementsByClass("date");
            if (!url.equals(Const.URL_MEDIA_REPORTS)){
                return dataFilterForList(links, dates, index);
            }else {
                return dataFilterForMedia(links, dates);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return null;
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
    private static List<Article> dataFilterForList(Elements links, Elements dates, int index){
        List<Article> articles = new ArrayList<>();
        int cirStart = index - 10;
        try {
            for (int i = cirStart; i < index; i++){
                Element link = links.get(i);
                Article article = new Article();
                article.setPublishTime(dates.get(i).text());
                article.setUrl(link.attr("href"));
                article.setTitle(link.text());
                // 获取文章内容
                Element contentNode = Jsoup.connect(article.getUrl()).get().body()
                        .select("[bgcolor=#FFFFFF]").get(0)
                        .getElementsByTag("tbody").get(0);
                if (contentNode.getElementsByTag("div").size() > 1){
                    article.setContent(contentNode.getElementsByTag("div").toString());
                }else {
                    article.setContent(contentNode.getElementsByTag("p").toString());
                }
                String summary = Jsoup.parse(article.getContent()).text();
                article.setSummary(summary);
                /**
                 * 获取第一张图片链接作为缩略图
                 * 如果为空，则不获取，防止抛出异常
                 */
                Elements imgLinks = Jsoup.parse(article.getContent()).getElementsByTag("img");
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
            return dataFilterForBanner(elements);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }

    private static List<Article> dataFilterForBanner(Elements linkTags){
        List<Article> articles = new ArrayList<>();
        try {
            for (Element linkTag: linkTags){
                Article article = new Article();
                article.setTitle(linkTag.getElementsByTag("img").get(0).attr("alt"));
                article.setThumbnail(linkTag.getElementsByTag("img").get(0).attr("abs:src"));
                article.setUrl(linkTag.getElementsByTag("a").get(0).attr("href"));
                String content = Jsoup.connect(article.getUrl()).get().body()
                        .select("[bgcolor=#FFFFFF]").get(0)
                        .getElementsByTag("tbody").get(0)
                        .getElementsByTag("p").toString();
                article.setContent(content);
                String summary = Jsoup.parse(content).text();
                article.setSummary(summary);
                // 暂时使用 URL 作为 id
                article.setId(article.getUrl());
                articles.add(article);
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return articles;
    }

    private static List<Article> dataFilterForMedia(Elements links, Elements dates){
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < links.size(); i++){
            Element link = links.get(i);
            Article article = new Article();
            article.setPublishTime(dates.get(i).text());
            article.setUrl(link.attr("href"));
            article.setTitle(link.text());
            article.setId(article.getUrl());
            articles.add(article);
        }
        return articles;
    }
}
