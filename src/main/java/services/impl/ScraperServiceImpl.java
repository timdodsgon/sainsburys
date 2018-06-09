package services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.Results;
import models.Product;
import models.Total;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ScraperService;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 6/6/2018.
 */
public class ScraperServiceImpl implements ScraperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScraperServiceImpl.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String RELATIVE_LINK = "../";

    private ObjectMapper objectMapper = new ObjectMapper();

    public String scrape(final String baseURL, final String path) throws IOException {

        Results results = new Results();
        List<Product> products = new ArrayList<>();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        LOGGER.info("Starting scrape");

        double runningTotal = 0;
        for(String link : getSainsburysProductLinks(new URL(baseURL + path))) {
            Product product = getSainsburysProduct(new URL(baseURL + link.replace(RELATIVE_LINK, StringUtils.EMPTY)));
            if(null != product) {
                runningTotal += product.getUnitPrice();
                products.add(product);
            } else {
                LOGGER.error("The following link {} returned no product data", link);
            }
        }

        results.setResults(products);
        results.setTotal(calculateVATFromRunningTotal(runningTotal));

        return objectMapper.writeValueAsString(results);
    }

    /**
     * Instantiates Total object and calculates the VAT in runningTotal
     *
     * @param runningTotal
     * @return
     */
    protected Total calculateVATFromRunningTotal(final double runningTotal) {
        Total total = new Total();

        total.setGross(BigDecimal.valueOf(runningTotal).setScale(2, RoundingMode.HALF_UP));
        total.setVat(BigDecimal.valueOf(runningTotal - (runningTotal/1.2)).setScale(2, RoundingMode.HALF_UP));
        return total;
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

        if(HttpsURLConnection.HTTP_OK == response.statusCode()) {
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
    protected Product getSainsburysProduct(final URL url) throws IOException {

        String title;
        Double unitPrice;
        Double calories;
        String description;

        Response response = Jsoup.connect(url.toString())
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .execute();

        if(HttpsURLConnection.HTTP_OK == response.statusCode()) {
            Document doc = response.parse();
            Element element = doc.select("div.productTitleDescriptionContainer").first();

            /* Product title */
            if(element == null) { return null; }
            title = element.getElementsByTag("h1").first().text();

            /* Calories per 100 grams */
            element = doc.select("td.nutritionLevel1, td:eq(0)[class], tr:eq(1) td:eq(1)").first();
            if(element == null) {
                calories = null;
            } else {
                calories = Double.parseDouble(element.text().replace("kcal", StringUtils.EMPTY));
            }

            /* Product price per unit */
            element = doc.select("p.pricePerUnit").first();
            if (element == null) { return null; }
            unitPrice = Double.parseDouble(element.text().substring(1).replace("/unit", StringUtils.EMPTY));
            
            /* Product description */
            element = doc.select("div.productText p").first();
            if (element == null) { return null;}
            description = element.text();

            return new Product(title, calories, unitPrice, description);
        }
        throw new HttpStatusException("Invalid response", response.statusCode(), url.toString());
    }

    /**
     * Method to aid in running the test harness
     *
     * @param objectMapper
     */
    protected void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
