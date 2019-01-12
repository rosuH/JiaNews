package me.rosuh.jianews.network;

import android.util.Log;

import android.util.Pair;

import java.util.ListIterator;
import java.util.WeakHashMap;
import me.rosuh.jianews.util.Const.PageURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.rosuh.jianews.bean.ArticleBean;
import me.rosuh.jianews.util.Const;
import me.rosuh.jianews.util.StringUtils;

/**
 * @author rosuh
 */
public class WebSpider {
    private static final int ARTICLES_COUNT_PER_PAGE = 50;
    private static int ARTICLES_LIST_PER_QUEUE = 10;
    private static final String TAG = "WebSpider";
    private static WeakHashMap<String, List<String>> mURLListWeakHashMap = new WeakHashMap<>();
    /**
     * 功能：根据传入的 url 和 index，进行文章数据的获取
     * 1. index 有两种情况
     * - index 为 0，这个时候是刷新或者初次载入列表的时候获取的，代表获取网页第一页，前 10 篇文章
     * - index 不为零，此时是用来加载更多的时候传入的列表最后一个 item 的 position
     * 2. 如果到了 50 条件记录，也就是 index == 5 的时候，获取的网页链接索引会递增
     *
     * @param pageURL   传入的网页链接
     * @param index 传入的索引值
     * @return 获取的文章列表
     */
    public static List<ArticleBean> getArticlesList(Const.PageURL pageURL, int index){
        String url = StringUtils.INSTANCE.getCorrectUrl(pageURL);

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
                return dataFilterForMedia(links, dates, urlIndexPair.second);
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
        if (links.size() < 10){
            index = links.size();
        }
        ListIterator<Element> elementListIterator = links.listIterator();
        while (elementListIterator.hasNext()){
            if (!elementListIterator.hasPrevious()){
                elementListIterator.next();
                continue;
            }
            String preStr = elementListIterator.previous().attr("abs:href" );
            elementListIterator.next();
            String nextStr = elementListIterator.next().attr("abs:href" );
            if (preStr.equals(nextStr)){
                elementListIterator.remove();
            }
        }

        try {
            for (int i = cirStart; i < index; i++) {
                Element currentLink = links.get(i);
                Element currentDate = dates.get(i);
                ArticleBean articleBean = new ArticleBean();
                articleBean.setDate(currentDate.text());
                articleBean.setUrl(currentLink.attr("abs:href"));
                articleBean.setTitle(currentLink.text());
                Element contentBody = Jsoup
                        .connect(articleBean.getUrl()).get().body();
                Element contentTableNode = contentBody
                        .select("body > table > tbody > tr:nth-child(3) > td > table > tbody")
                        .first();
                contentBody.children().first().remove();
                Elements realContent = contentTableNode.select("p,span");
                int lastClickJsIndex = realContent.toString().indexOf("发布日期");
                // 过滤发布日期后面的字符串
                if (lastClickJsIndex > -1){
                    articleBean.setContent(realContent.toString().substring(0, lastClickJsIndex));
                } else {
                    articleBean.setContent(realContent.toString());
                }
                articleBean.setSummary(realContent.text().substring(0, 20));

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


    private static List<ArticleBean> dataFilterForMedia(Elements links, Elements dates, int index){
        List<ArticleBean> articleBeans = new ArrayList<>();
        int cirStart = index - 10;
        if (links.size() < 10){
            index = links.size();
        }
        for (int i = cirStart; i < index; i++){
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

        List<String> pages = mURLListWeakHashMap.get(homePageUrl);
        if (pages == null){
            pages = new ArrayList<>();
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
            mURLListWeakHashMap.put(homePageUrl, pages);
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
            pos = pos + ARTICLES_LIST_PER_QUEUE;
        }else {
            // 当文章索引值超过了 50，那么需要请求下一页，此时 pos 需通过页码的倍数进行计算
            pos = pos + ARTICLES_LIST_PER_QUEUE - ARTICLES_COUNT_PER_PAGE * pageCount;
        }

        return new Pair<>(pageUrl, pos);

    }
}
