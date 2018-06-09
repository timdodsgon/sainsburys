package services.factories;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import services.BaseScaperServiceTest;
import services.ScraperService;
import services.impl.Sainsburys;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScraperServiceFactoryTest extends BaseScaperServiceTest {

    @Mock
    private Appender appender;

    ScraperServiceFactory scraperServiceFactory;

    @Before
    public void setup(){
        scraperServiceFactory = new ScraperServiceFactory();

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);

        when(appender.getName()).thenReturn("MOCK");
        when(appender.isStarted()).thenReturn(true);
    }

    @Test
    public void testScraperServiceFactoryReturnsGivenScraper(){
        // given
        ScraperService scraperService = scraperServiceFactory.getScraperService("SAINSBURYS");
        // then
        assertThat(scraperService, instanceOf(Sainsburys.class));
        assertThatLoggerMessageIs(appender,1,"Returning Sainsburys scraper");
    }

    @Test
    public void testScraperServiceFactoryReturnsNullGivenUnknownScraper(){
        // given
        ScraperService scraperService = scraperServiceFactory.getScraperService("UNKNOWN_SCRAPER");
        // then
        assertThat(scraperService, is(nullValue()));
        assertThatLoggerMessageIs(appender,1,"Sorry no scrapers available for UNKNOWN_SCRAPER");
    }
}
