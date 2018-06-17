package services.output;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import models.Results;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import services.BaseTest;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XMLOutputTest extends BaseTest {

    private XMLOutput underTest;

    @Mock
    private Appender appender;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        underTest = new XMLOutput();

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);

        when(appender.getName()).thenReturn("MOCK");
        when(appender.isStarted()).thenReturn(true);
    }

    @Test
    public void testXMLOutputReturnsExpectedXML() throws IOException, SAXException {
        // given
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/expected.xml"), "UTF-8");
        Results results = givenModelObjects();
        // when
        String actual = underTest.output(results);
        // then
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(expected, actual);
        assertThatLoggerMessageIs(appender, 1, "Generating XML response");
        assertThatLoggerMessageIs(appender, 1, "XML is valid");
    }

    @Test
    public void testThrowsJsonProcessingException() throws IOException {
        // given
        XmlMapper xmlMapper = Mockito.spy(new XmlMapper());
        when(xmlMapper.writeValueAsString(any(Object.class))).thenThrow(new JsonProcessingException("Error") {
        });
        underTest.setXMLMapper(xmlMapper);
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
        XmlMapper spyXmlMapper = Mockito.spy(new XmlMapper());
        when(spyXmlMapper.writeValueAsString(any(Object.class))).thenReturn("{{]");
        underTest.setXMLMapper(spyXmlMapper);
        Results results = givenModelObjects();
        // when
        String xml = underTest.output(results);
        // then
        assertThat(xml, is("<Results>INVALID</Results>"));
        assertThatLoggerMessageIs(appender, 1, "Generating XML response");
        assertThatLoggerMessageIs(appender, 1, "XML is invalid {}");
    }

    @Test
    public void testIsJSONValidReturnsTrueWithValidJSON() throws IOException {
        // given
        String xml = IOUtils.toString(this.getClass().getResourceAsStream("/expected.xml"), "UTF-8");
        // when
        boolean isValid = underTest.isXMLValid(xml);
        // then
        assertThat(isValid, is(true));
        assertThatLoggerMessageIs(appender, 1, "XML is valid");
    }

    @Test
    public void testIsJSONValidReturnsFalseWithInValidJSON() {
        // given
        String xml = "{{]";
        // when
        boolean isValid = underTest.isXMLValid(xml);
        // then
        assertThat(isValid, is(false));
        assertThatLoggerMessageIs(appender, 1, "XML is invalid {}");
    }
}
