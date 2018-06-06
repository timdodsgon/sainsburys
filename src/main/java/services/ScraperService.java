package services;

import models.SainsburysData;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tim on 6/6/2018.
 */
public class ScraperService {

    private static final int HTTP_STATUS_OK = 200;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";

    /**
     * Scrape product links from Sainsbury's web page
     *
     * @param url
     * @return
     */
    public List<String> getSainsburysProductLinks(String url) throws IOException {


        Response response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();

        if(HTTP_STATUS_OK == response.statusCode()) {
            return response.parse().select(".productNameAndPromotions a").eachAttr("href");
        }
        throw new HttpStatusException("Invalid response", response.statusCode(), url);
    }

    /**
     * Scrape product information Sainsbury's
     *
     * @param url
     * @return
     */
    public SainsburysData getSainsburysProductData(String url) throws IOException {
        return new SainsburysData("Berrys","20", "1.50", "Really nice berry's");
    }
}
