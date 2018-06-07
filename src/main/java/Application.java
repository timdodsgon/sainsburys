import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.Results;
import models.SainsburysData;
import models.Total;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import services.ScraperService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final String PATH = "webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
    private static final String BASE_URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/";

    public static void main(String[] args) {
        ScraperService scraperService = new ScraperService();
        List<SainsburysData> sainsburysData = new ArrayList<>();
        Total total = new Total();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            double gross = 0;
            URL url = new URL(BASE_URL + PATH);

            for(String link : scraperService.getSainsburysProductLinks(url)) {
                SainsburysData sd = scraperService.getSainsburysProductData(new URL(BASE_URL + StringUtils.substringAfterLast(link, "../")));
                gross += sd.getUnitPrice();
                sainsburysData.add(sd);
            }

            total.setGross(BigDecimal.valueOf(gross).setScale(2));
            total.setVat(BigDecimal.valueOf(gross - (gross/1.2)).setScale(2, RoundingMode.HALF_UP));

            Results results = new Results();
            results.setResults(sainsburysData);
            results.setTotal(total);

            String arrayToJson = objectMapper.writeValueAsString(results);

            System.out.println(arrayToJson);

        } catch (IOException e) {
            if(e instanceof JsonProcessingException) {
                //@TODO
            } else if (e instanceof HttpStatusException) {
                //@TODO
            } else if (e instanceof MalformedURLException) {
                //@TODO
            }
        }
    }
}
