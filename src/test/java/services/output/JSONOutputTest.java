package services.output;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Results;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.LoggerFactory;
import services.BaseTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class JSONOutputTest extends BaseTest {

    private JSONOutput underTest;

    @Mock
    private Appender appender;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        underTest = new JSONOutput();

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);

        when(appender.getName()).thenReturn("MOCK");
        when(appender.isStarted()).thenReturn(true);
    }

    @Test
    public void testJSONOutputReturnsExpectedJSON() throws IOException, JSONException {
        // given
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/expected.json"), "UTF-8");
        Results results = givenModelObjects();
        // when
        String actual = underTest.output(results);
        // then
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
        assertThatLoggerMessageIs(appender, 1, "Generating JSON response");
        assertThatLoggerMessageIs(appender, 1, "JSON is valid");
    }

    @Test
    public void testThrowsJsonProcessingException() throws IOException {
        // given
        ObjectMapper spyObjectMapper = Mockito.spy(new ObjectMapper());
        when(spyObjectMapper.writeValueAsString(any(Object.class))).thenThrow(new JsonProcessingException("Error") {
        });
        underTest.setObjectMapper(spyObjectMapper);
        Results results = givenModelObjects();
        // expected
        exception.expect(JsonProcessingException.class);
        exception.expectMessage("Error");
        // when
        underTest.output(results);
    }

    @Test
    public void testInValidJsonReturnsTheInValidJsonResponse() throws IOException {
        // given
        ObjectMapper spyObjectMapper = Mockito.spy(new ObjectMapper());
        when(spyObjectMapper.writeValueAsString(any(Object.class))).thenReturn("{{]");
        underTest.setObjectMapper(spyObjectMapper);
        Results results = givenModelObjects();
        // when
        String json = underTest.output(results);
        // then
        assertThat(json, is("{ \"results\" : \"INVALID\" }"));
        assertThatLoggerMessageIs(appender, 1, "Generating JSON response");
        assertThatLoggerMessageIs(appender, 1, "JSON is invalid {}");
    }

    @Test
    public void testIsJSONValidReturnsTrueWithValidJSON() throws IOException {
        // given
        String json = IOUtils.toString(this.getClass().getResourceAsStream("/expected.json"), "UTF-8");
        // when
        boolean isValid = underTest.isJSONValid(json);
        // then
        assertThat(isValid, is(true));
        assertThatLoggerMessageIs(appender, 1, "JSON is valid");
    }

    @Test
    public void testIsJSONValidReturnsFalseWithInValidJSON() {
        // given
        String json = "{{]";
        // when
        boolean isValid = underTest.isJSONValid(json);
        // then
        assertThat(isValid, is(false));
        assertThatLoggerMessageIs(appender, 1, "JSON is invalid {}");
    }
}
