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
import services.OutputService;
import services.output.JSONOutput;
import services.output.XMLOutput;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutputServiceFactoryTest extends BaseTest {

    @Mock
    private Appender appender;

    @Before
    public void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);

        when(appender.getName()).thenReturn("MOCK");
        when(appender.isStarted()).thenReturn(true);
    }

    @Test
    public void testOutputServiceFactoryReturnsJSONOutputWhenRequested() {
        // given
        OutputService outputService = OutputServiceFactory.getOutputService("JSON");
        // then
        assertThat(outputService, instanceOf(JSONOutput.class));
        assertThatLoggerMessageIs(appender, 1, "Outputting JSON");
    }

    @Test
    public void testOutputServiceFactoryReturnsXMLOutputWhenRequested() {
        // given
        OutputService outputService = OutputServiceFactory.getOutputService("XML");
        // then
        assertThat(outputService, instanceOf(XMLOutput.class));
        assertThatLoggerMessageIs(appender, 1, "Outputting XML");
    }

    @Test
    public void testScraperServiceFactoryReturnsNullGivenUnknownScraper() {
        // given
        OutputService outputService = OutputServiceFactory.getOutputService("UNKNOWN_OUTPUT");
        // then
        assertThat(outputService, is(nullValue()));
        assertThatLoggerMessageIs(appender, 1, "Sorry no output types for given request UNKNOWN_OUTPUT");
    }
}
