
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ScraperService;
import services.impl.ScraperServiceImpl;

import java.io.IOException;
import java.net.MalformedURLException;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static final String BASE_URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/";
    private static final String PATH = "webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";

    public static void main(String[] args) {
        ScraperService scraperService = new ScraperServiceImpl();

        try {
            System.out.println(scraperService.scrape(BASE_URL, PATH));
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
