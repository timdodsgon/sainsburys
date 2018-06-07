package services;

import models.SainsburysData;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Tim on 6/6/2018.
 */
public class ScraperService {

    private static final int HTTP_STATUS_OK = 200;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String EMPTY_STRING = "";

    /**
     * Scrape product links from Sainsbury's web page
     *
     * @param url
     * @return
     */
    public List<String> getSainsburysProductLinks(URL url) throws IOException {

        Response response = Jsoup.connect(url.toString())
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();

        if(HTTP_STATUS_OK == response.statusCode()) {
            return response.parse().select(".productNameAndPromotions a").eachAttr("href");
        }
        throw new HttpStatusException("Invalid response", response.statusCode(), url.toString());
    }

    /**
     * Scrape product information Sainsbury's product page
     *
     * @param url
     * @return
     */
    public SainsburysData getSainsburysProductData(URL url) throws IOException {

        String title;
        Double unitPrice;
        String description;

        Response response = Jsoup.connect(url.toString())
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .execute();

        if(HTTP_STATUS_OK == response.statusCode()) {
            Document doc = response.parse();
            Element el = doc.select("div.productTitleDescriptionContainer").first();

            /* Product title */
            if (el == null) {
                return null;
            } else {
                title = el.getElementsByTag("h1").first().text();
            }

            /* Product price per unit */
            el = doc.select("p.pricePerUnit").first();
            if (el == null) {
                return null;
            } else {
                unitPrice = Double.parseDouble(el.text().replace("/unit", EMPTY_STRING).replace("£", EMPTY_STRING));
            }

            /* Product description */
            el = doc.select("div.productText p").first();
            if (el == null) {
                return null;
            } else {
                description = el.text();
            }

            return new SainsburysData(title,null, unitPrice, description);
        }
        throw new HttpStatusException("Invalid response", response.statusCode(), url.toString());
    }
}
