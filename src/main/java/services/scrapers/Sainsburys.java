package services.scrapers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import config.Config;
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

public class Sainsburys implements ScraperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sainsburys.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String RELATIVE_LINK = "../";
    private static final String INVALID_JSON = "{ \"results\" : \"INVALID\" }";
    private static final String DECIMAL_REGEX = "[^0-9.]";

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gather information from a http response and return a JSON String
     *
     * @param config
     * @return JSON String
     * @throws IOException
     */
    public String scrape(Config config) throws IOException {

        List<Product> products = new ArrayList<>();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        LOGGER.info("Starting scrape");

        double runningTotal = 0;
        for(String link : getSainsburysProductLinks(new URL(config.getUrl()))) {
            Product product = getSainsburysProduct(new URL(config.getBaseURL() + link.replace(RELATIVE_LINK, StringUtils.EMPTY)), config);
            if(null != product) {
                runningTotal += product.getUnitPrice().doubleValue();
                products.add(product);
            } else {
                LOGGER.error("The following link {} returned no product data", link);
            }
        }

        Results results = new Results(products, calculateVATFromRunningTotal(runningTotal));

        String jsonString = objectMapper.writeValueAsString(results);
        if(isJSONValid(jsonString)){
            return jsonString;
        }
        return INVALID_JSON;
    }

    /**
     * Instantiates Total object and calculates the VAT in runningTotal
     *
     * @param productsTotal
     * @return
     */
    Total calculateVATFromRunningTotal(final double productsTotal) {
        return new Total(BigDecimal.valueOf(productsTotal).setScale(2, RoundingMode.HALF_UP),
                         BigDecimal.valueOf(productsTotal - (productsTotal/1.2)).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Scrape product links from Sainsbury's web page
     *
     * @param url
     * @return
     */
    List<String> getSainsburysProductLinks(final URL url) throws IOException {

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
    Product getSainsburysProduct(final URL url, final Config config) throws IOException {

        String title;
        BigDecimal unitPrice;
        Double calories;
        String description;

        Response response = Jsoup.connect(url.toString())
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .execute();

        if(HttpsURLConnection.HTTP_OK == response.statusCode()) {
            Document doc = response.parse();
            Element element;

            /* Product title */
            element = doc.select(config.getTitleCSSSelector()).first();
            if(element == null) { return null; }
            title = element.text();

            /* Calories per 100 grams */
            element = doc.select(config.getCaloriesCSSSelector()).first();
            if(element == null) {
                calories = null;
            } else {
                calories = Double.parseDouble(element.text().replace("kcal", StringUtils.EMPTY));
            }

            /* Product price per unit */
            element = doc.select(config.getPriceCSSSelector()).first();
            if (element == null) { return null; }
            unitPrice = BigDecimal.valueOf(Double.parseDouble(element.text().replaceAll(DECIMAL_REGEX,
                                           StringUtils.EMPTY))).setScale(2, RoundingMode.HALF_UP);

            /* Product description */
            element = doc.select(config.getDescriptionCSSSelector()).first();
            if (element == null) { return null;}
            description = element.text();

            return new Product(title, calories, unitPrice, description);
        }
        throw new HttpStatusException("Invalid response", response.statusCode(), url.toString());
    }

    /**
     * Test given JSON is valid
     *
     * @param json
     * @return boolean
     */
    boolean isJSONValid(String json) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Method to aid in running the test harness
     *
     * @param objectMapper
     */
    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
