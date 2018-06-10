
import com.fasterxml.jackson.core.JsonProcessingException;
import config.Config;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ScraperService;
import services.factories.ScraperServiceFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * Application to retrieve values from a http response
 * Optionally a properties file can be used to override the default settings
 *
 * To use an external properties file, values need to be in the following format
 * name=value once per line
 *
 * Overridable values are
 * url
 * baseurl
 * title
 * calories
 * price
 * description
 *
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    /**
     * Runs the application and handle errors
     * @param args
     */
    public static void main(String[] args) {

        /*
         * Optionally pass a properties file at run time or use the
         * internal default values
         */
        Config config = getConfiguration(args);

        ScraperServiceFactory scraperServiceFactory = new ScraperServiceFactory();
        ScraperService scraperService = scraperServiceFactory.getScraperService("SAINSBURYS");

        try {
            if(null != scraperService)
                System.out.println(scraperService.scrape(config));
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

    /**
     * Returns a Config object with default settings or values from a properties file
     *
     * @param args
     * @return
     */
    private static Config getConfiguration(String[] args) {
        if(args.length > 0) {
            try (FileInputStream fileInputStream = new FileInputStream(args[0])) {
                Properties properties = new Properties();
                properties.load(fileInputStream);
                LOGGER.info("Properties successfully loaded from {}", args[0]);
                return new Config(properties);
            } catch (IOException e) {
                LOGGER.error("File not found {}", args[0]);
            }
        }
        return new Config();
    }
}
