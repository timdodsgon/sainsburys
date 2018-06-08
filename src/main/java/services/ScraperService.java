package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.Results;
import models.SainsburysData;
import models.Total;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 6/6/2018.
 */
public class ScraperService {

    private static final int HTTP_STATUS_OK = 200;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String EMPTY_STRING = "";
    private static final String SEPARATOR = "../";

    public String scrape(final String domain, final String path) throws IOException {

        ScraperService scraperService = new ScraperService();
        Total total = new Total();
        Results results = new Results();

        List<SainsburysData> sainsburysData = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        double gross = 0;
        for(String link : scraperService.getSainsburysProductLinks(new URL(domain + path))) {
            SainsburysData sd = scraperService.getSainsburysProductData(new URL(domain + link.replace(SEPARATOR, EMPTY_STRING)));
            gross += sd.getUnitPrice();
            sainsburysData.add(sd);
        }

        populateTotal(total, gross);

        results.setResults(sainsburysData);
        results.setTotal(total);

        return objectMapper.writeValueAsString(results);
    }

    protected void populateTotal(final Total total, final double gross) {
        total.setGross(BigDecimal.valueOf(gross).setScale(2));
        total.setVat(BigDecimal.valueOf(gross - (gross/1.2)).setScale(2, RoundingMode.HALF_UP));
    }


    /**
     * Scrape product links from Sainsbury's web page
     *
     * @param url
     * @return
     */
    protected List<String> getSainsburysProductLinks(final URL url) throws IOException {

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
    protected SainsburysData getSainsburysProductData(final URL url) throws IOException {

        String title;
        Double unitPrice;
        Double calories;
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

            /* Calories per 100 grams */
            el = doc.select("td.nutritionLevel1, td:eq(0)[class], tr:eq(1) td:eq(1)").first();
            if (el == null) {
                calories = null;
            } else {
                calories = Double.parseDouble(el.text().replace("kcal", ""));
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

            return new SainsburysData(title, calories, unitPrice, description);
        }
        throw new HttpStatusException("Invalid response", response.statusCode(), url.toString());
    }
}
