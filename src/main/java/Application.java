
import com.fasterxml.jackson.core.JsonProcessingException;
import config.Config;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ScraperService;
import services.factories.ScraperServiceFactory;

import java.io.IOException;
import java.net.MalformedURLException;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        ScraperServiceFactory scraperServiceFactory = new ScraperServiceFactory();
        ScraperService scraperService = scraperServiceFactory.getScraperService("SAINSBURYS");

        try {
            if(null != scraperService)
                System.out.println(scraperService.scrape(Config.URL, Config.BASE_URL));
        } catch (IOException e) {
            if(e instanceof JsonProcessingException)
                LOGGER.error("There has been an error processing your JSON objects", e);
            else if (e instanceof HttpStatusException)
                LOGGER.error("Invalid HTTP Status code", e);
            else if (e instanceof MalformedURLException)
                LOGGER.error("The url being used is malformed", e);
            else
                LOGGER.error("There has been an io exception", e);
        }
    }
}
