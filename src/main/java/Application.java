import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import models.Results;
import models.SainsburysData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import services.ScraperService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application{

    private static final String URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
    private static final String BASE_URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/";

    public static void main(String[] args) {
        ScraperService scraperService = new ScraperService();
        List<SainsburysData> sainsburysData = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            for(String link : scraperService.getSainsburysProductLinks(URL)) {
                sainsburysData.add(scraperService.getSainsburysProductData(BASE_URL + StringUtils.substringAfterLast(link, "../")));
            }

            Results results = new Results();
            results.setResults(sainsburysData);

            String arrayToJson = objectMapper.writeValueAsString(results);

            System.out.println(arrayToJson);

        } catch (IOException e) {
            if(e instanceof JsonProcessingException) {
                //@TODO
            } else if (e instanceof HttpStatusException) {
                //@TODO
            }
        }
    }
}
