package services.factories;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import services.BaseTest;
import services.ScraperService;
import services.scrapers.Sainsburys;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScraperServiceFactoryTest extends BaseTest {

    @Mock
    private Appender appender;

    @Before
    public void setup(){
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);

        when(appender.getName()).thenReturn("MOCK");
        when(appender.isStarted()).thenReturn(true);
    }

    @Test
    public void testScraperServiceFactoryReturnsGivenScraper(){
        // given
        ScraperService scraperService = ScraperServiceFactory.getScraperService("SAINSBURYS");
        // then
        assertThat(scraperService, instanceOf(Sainsburys.class));
        assertThatLoggerMessageIs(appender,1,"Returning Sainsburys scraper");
    }

    @Test
    public void testScraperServiceFactoryReturnsNullGivenUnknownScraper(){
        // given
        ScraperService scraperService = ScraperServiceFactory.getScraperService("UNKNOWN_SCRAPER");
        // then
        assertThat(scraperService, is(nullValue()));
        assertThatLoggerMessageIs(appender,1,"Sorry no scrapers available for UNKNOWN_SCRAPER");
    }
}
