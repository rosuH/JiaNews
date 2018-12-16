package me.rosuh.jianews.network;

import android.util.Log;

import android.util.Pair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.rosuh.android.jianews.Const.URL_CAMPUS_ACTIVITIES;
import static me.rosuh.android.jianews.Const.URL_CAMPUS_ANNOUNCEMENT;
import static me.rosuh.android.jianews.Const.URL_MAJOR_NEWS;
import static me.rosuh.android.jianews.Const.URL_MEDIA_REPORTS;

/**
 * @author rosuh
 */
public class WebSpider {
    private static final int ARTICLES_COUNT_PER_PAGE = 50;
    private static final int ARTICLES_LIST_PER_QUEUE = 10;
    /**
     * 功能：根据传入的 url 和 index，进行文章数据的获取
     * 1. index 有两种情况
     * - index 为 0，这个时候是刷新或者初次载入列表的时候获取的，代表获取网页第一页，前 10 篇文章
     * - index 不为零，此时是用来加载更多的时候传入的列表最后一个 item 的 position
     * 2. 如果到了 50 条件记录，也就是 index == 5 的时候，获取的网页链接索引会递增
     *
     * @param url   传入的网页链接
     * @param index 传入的索引值
     * @return 获取的文章列表
     */
    public static List<ArticleBean> getArticlesList(Const.PageURL pageURL, int index){
        // 链接判断和页码判断
//        boolean isUrlPointless = !url.equals(URL_MAJOR_NEWS) &&
//                !url.equals(URL_CAMPUS_ACTIVITIES) &&
//                !url.equals(URL_MEDIA_REPORTS) &&
//                !url.equals(URL_CAMPUS_ANNOUNCEMENT);

        String mUrl;
        String url = StringUtils.INSTANCE.getCorrectUrl(pageURL);

        // 传入的 position 总是为 9、19、29，所以需要 +1 以便判断
        int count = (index + 1) / ARTICLES_COUNT_PER_PAGE;
        Log.i(TAG, "getArticlesList: position =========>>> " + index);
        Log.i(TAG, "getArticlesList: count =========>>> " + count);

        // 先获取可用的页码链接
        List<String> pagesLinks = getPagesLinks(url);

        if (pagesLinks == null || pagesLinks.isEmpty()){
            return null;
        }

        Pair<String, Integer> urlIndexPair = getCurrentPageLink(index, pagesLinks);


        try {
            Document doc = Jsoup.connect(urlIndexPair.first).get();
            Element uls = doc.getElementsByTag("ul").get(0);
            Elements links = uls.getElementsByTag("a");

            Elements dates = doc.getElementsByClass("date");
            if (!url.equals(Const.URL_MEDIA_REPORTS)) {
                return dataFilterForList(links, dates, urlIndexPair.second);
            } else {
                return dataFilterForMedia(links, dates);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     * 功能：传入已获取的文章链接集合，在此方法内执行数据获取和对 sArticleList 的数据填充
     * 1. id 为链接的尾 5 位数字
     * 2. title 直接获取
     * 3. url 直接获取
     * 4. content
     *
     * @param links 文章链接集合
     * @return articles 返回已经填充好的文章列表
     */
    private static List<ArticleBean> dataFilterForList(Elements links, Elements dates, int index){
        List<ArticleBean> articleBeans = new ArrayList<>();
        int cirStart = index - 10;
        try {
            for (int i = cirStart; i < index; i++) {
                Element link = links.get(i);
                ArticleBean articleBean = new ArticleBean();
                articleBean.setPublishTime(dates.get(i).text());
                articleBean.setUrl(link.attr("abs:href"));
                articleBean.setTitle(link.text());
                Element contentBody = Jsoup
                        .connect(articleBean.getUrl()).get().body();
                Element contentTableNode = contentBody
                        .select("body > table > tbody > tr:nth-child(3) > td > table > tbody")
                        .first();
                contentBody.children().first().remove();
                Elements realContent = contentTableNode.select("p,span");
                articleBean.setContent(realContent.toString());
                articleBean.setSummary(realContent.text());

//                if (contentTableInsideTr.getElementsByTag("div").size() > 1){
////                    articleBean.setContent(contentTableInsideTr.getElementsByTag("div").toString());
////                }else {
////                    articleBean.setContent(contentTableInsideTr.getElementsByTag("p").toString());
////                }
                /**
                 * 获取第一张图片链接作为缩略图
                 * 如果为空，则不获取，防止抛出异常
                 */
                Elements imgLinks = contentTableNode.getElementsByTag("img");
                if (!imgLinks.isEmpty()) {
                    articleBean.setThumbnail(imgLinks.get(0).attr("abs:src"));
                }
                String idStr = articleBean.getUrl();
                int dotIndex = idStr.lastIndexOf(".");
                int spaIndex = idStr.lastIndexOf("/");
                idStr = idStr.substring(spaIndex + 1, dotIndex);
                articleBean.setId(idStr);

                articleBeans.add(articleBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleBeans;
    }

    public static List<ArticleBean> getBannerList(){
        try {
            Elements elements = Jsoup.connect(Const.URL_HOME_PAGE)
                    .get()
                    .select("td#demo1").get(0)
                    .select("div.best-pic");
            return dataFilterForBanner(elements);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private static List<ArticleBean> dataFilterForBanner(Elements linkTags) {
        List<ArticleBean> articles = new ArrayList<>();
        try {
            for (Element linkTag : linkTags) {
                ArticleBean articleBean = new ArticleBean();
                articleBean.setTitle(linkTag.getElementsByTag("img").get(0).attr("alt"));
                articleBean.setThumbnail(linkTag.getElementsByTag("img").get(0).attr("abs:src"));
                articleBean.setUrl(linkTag.getElementsByTag("a").get(0).attr("href"));
                String content = Jsoup.connect(Const.URL_HOME_PAGE + "/" + article.getUrl()).get().body()
                        .select("[bgcolor=#FFFFFF]").get(0)
                        .getElementsByTag("tbody").get(0)
                        .getElementsByTag("p").toString();
                articleBean.setContent(content);
                String summary = Jsoup.parse(content).text();
                articleBean.setSummary(summary);
                // 暂时使用 URL 作为 id
                articleBean.setId(articleBean.getUrl());
                articleBeans.add(articleBean);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return articleBeans;
    }

    private static List<ArticleBean> dataFilterForMedia(Elements links, Elements dates){
        List<ArticleBean> articleBeans = new ArrayList<>();
        for (int i = 0; i < links.size(); i++){
            Element link = links.get(i);
            ArticleBean articleBean = new ArticleBean();
            articleBean.setDate(dates.get(i).text());
            articleBean.setUrl(link.attr("href"));
            articleBean.setTitle(link.text());
            articleBean.setId(articleBean.getUrl());
            articleBeans.add(articleBean);
        }
        return articleBeans;
    }

    /**
     * 获取页面下面页码的所有可用链接
     * @param homePageUrl   当前页面的链接
     * @return  可用的页码链接
     */
    private static List<String> getPagesLinks(String homePageUrl) {
        List<String> pages = new ArrayList<>();
        pages.add(homePageUrl + ".htm");
        try {

            Element currentBody = Jsoup.connect(homePageUrl + ".htm").get().body();

            for (Element element : currentBody.select(".p_no")) {
                pages.add(
                        element.getElementsByTag("a")
                                .first()
                                .attr("abs:href")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pages;
    }

    /**
     * 通过 RecyclerView 传入的 pos 和可用的页码链接，得到当前应该使用的页码，比如 51 就是第二页
     * @param pos
     * @param pagesLinks
     * @return
     */
    private static Pair<String, Integer> getCurrentPageLink(int pos, List<String> pagesLinks){
        final int articlesListPerQueue = 10;

        // 传入的 position 总是为 9、19、29，所以需要 +1 以便判断
        int pageCount = (pos + 1) / ARTICLES_COUNT_PER_PAGE;
        boolean isMorePage = pageCount >= 1;
        String pageUrl;

        if (isMorePage){
            pageUrl = pagesLinks.get(pageCount);
        }else {
            pageUrl = pagesLinks.get(0);
        }

        if (pos == 0){
            // 文章索引为 0，也就是刷新或者首次载入列表的时候
            // 文章页链接为 第一页链接，pos 的值需要重设为 10，方便后面做循环
            pos += ARTICLES_LIST_PER_QUEUE;
        }else if (!isMorePage){
            // count 计算的是网页的页码，如果文章索引低于 50，那么就是在第一页之内
            // 因为 pos = 0 时，是为刷新的情况，所以 9 的时候，是为加载下一页的情况，为了方便循环，这里为之加上 10
            pos = pos + 1 + ARTICLES_LIST_PER_QUEUE;
        }else {
            // 当文章索引值超过了 50，那么需要请求下一页，此时 pos 需通过页码的倍数进行计算
            pos = pos + ARTICLES_LIST_PER_QUEUE - ARTICLES_COUNT_PER_PAGE * pageCount;
        }

        return new Pair<>(pageUrl, pos);

    }
}
