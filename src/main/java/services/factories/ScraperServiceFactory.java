package services.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ScraperService;
import services.scrapers.Sainsburys;

/**
 * Factory to return appropriate Scraper for a given site
 */
public class ScraperServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScraperServiceFactory.class);

    public ScraperService getScraperService(String scraper){
        if("SAINSBURYS".equals(scraper)) {
            LOGGER.info("Returning Sainsburys scraper");
            return new Sainsburys();
        }
        LOGGER.error("Sorry no scrapers available for {}", scraper);
        return null;
    }
}
