package services.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import models.Product;
import models.Total;
import org.json.JSONException;
import org.jsoup.HttpStatusException;
import org.junit.*;

import org.apache.commons.io.IOUtils;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScraperServiceImplTest {

    private static final String BASE_URL = "http://127.0.0.1:8089";
    private static final String PATH = "/products/products.html";
    private static final String MALFORMED_URL = "http://127.0.0.1:8089http/products/products.html";

    private ScraperServiceImpl scraperService;

    @Mock
    private Appender appender;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws IOException {

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory
                .getLogger(Logger.ROOT_LOGGER_NAME);
        when(appender.getName()).thenReturn("MOCK");
        when(appender.isStarted()).thenReturn(true);
        logger.addAppender(appender);

        scraperService = new ScraperServiceImpl();

        /* Mock products list */
        String productsHtml = IOUtils.toString(this.getClass().getResourceAsStream("/products.html"), "UTF-8");
        givenWebSiteResponse(productsHtml, "/products/products.html", HttpsURLConnection.HTTP_OK);

        /* Mock individual product pages */
        String berrysHtml = IOUtils.toString(this.getClass().getResourceAsStream("/berrys.html"), "UTF-8");
        givenWebSiteResponse(berrysHtml, "/products/berrys.html", HttpsURLConnection.HTTP_OK);

        String blueberriesHtml = IOUtils.toString(this.getClass().getResourceAsStream("/blueberries.html"), "UTF-8");
        givenWebSiteResponse(blueberriesHtml, "/products/blueberries.html", HttpsURLConnection.HTTP_OK);

        String strawberriesHtml = IOUtils.toString(this.getClass().getResourceAsStream("/strawberries.html"), "UTF-8");
        givenWebSiteResponse(strawberriesHtml, "/products/strawberries.html", HttpsURLConnection.HTTP_OK);
    }

    @Test
    public void testScrapeReturnsExpectedJSON() throws IOException, JSONException {
        // given
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/expected.json"), "UTF-8");
        // when
        String actual = scraperService.scrape(BASE_URL, PATH);
        // then
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
        assertThatLoggerMessageIs(1,"Starting scrape");
    }

    @Test
    public void testGetSainsburysProductLinksRedirectHTTPStatusThrowsHttpStatusException() throws IOException {
        // given
        String productsHtml = IOUtils.toString(this.getClass().getResourceAsStream("/berrys.html"), "UTF-8");
        givenWebSiteResponse(productsHtml, "/products/products.html", HttpsURLConnection.HTTP_MOVED_TEMP);
        // expected
        exception.expect(HttpStatusException.class);
        exception.expectMessage("Invalid response");
        // when
        scraperService.getSainsburysProductLinks(new URL(BASE_URL + PATH));
    }

    @Test
    public void testGetSainsburysProductRedirectHTTPStatusThrowsHttpStatusException() throws IOException {
        // given
        String productsHtml = IOUtils.toString(this.getClass().getResourceAsStream("/products.html"), "UTF-8");
        givenWebSiteResponse(productsHtml, "/products/berrys.html", HttpsURLConnection.HTTP_MOVED_TEMP);
        // expected
        exception.expect(HttpStatusException.class);
        exception.expectMessage("Invalid response");
        // when
        scraperService.getSainsburysProduct(new URL(BASE_URL + "/products/berrys.html"));
    }

    @Test
    public void testMalformedURLThrowsMalformedURLException() throws IOException {
        // expected
        exception.expect(MalformedURLException.class);
        exception.expectMessage("8089http");
        // when
        scraperService.scrape(MALFORMED_URL, PATH);
    }

    @Test
    public void testThrowsJsonProcessingException() throws IOException {
        // given
        ObjectMapper spyObjectMapper = Mockito.spy(new ObjectMapper());
        when(spyObjectMapper.writeValueAsString(any(Object.class))).thenThrow(new JsonProcessingException("Error"){});
        scraperService.setObjectMapper(spyObjectMapper);
        // expected
        exception.expect(JsonProcessingException.class);
        exception.expectMessage("Error");
        // when
        scraperService.scrape(BASE_URL, PATH);
    }

    @Test
    public void testGetSainsburysProductLinksReturnsCorrectNumberOfLinks() throws IOException {
        // when
        List<String> links = scraperService.getSainsburysProductLinks(new URL(BASE_URL + PATH));
        // then
        assertThat(3, is(links.size()));
    }

    @Test
    public void testGetSainsburysProductDataReturnsCorrectProductData() throws IOException {
        // when
        Product product = scraperService.getSainsburysProduct(new URL(BASE_URL + "/products/berrys.html"));
        // then
        assertThat("Sainsbury's Mixed Berry Twin Pack 200g", is(product.getTitle()));
        assertThat(product.getkCalPer100g(), is(nullValue()));
        assertThat(new Double(2.75), is(product.getUnitPrice()));
        assertThat("Mixed Berries", is(product.getDescription()));
    }

    @Test
    public void testCalculateVATFromRunningTotalGeneratesCorrectVATAmount() throws IOException {
        // given
        double runningTotal = 40;
        double expectedVAT = 6.67;
        // when
        Total total = scraperService.calculateVATFromRunningTotal(runningTotal);
        // then
        assertThat(BigDecimal.valueOf(expectedVAT), is(total.getVat()));
    }

    private void givenWebSiteResponse(String productsHtml, String url, int httpStatus) {
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                .withStatus(httpStatus)
                .withHeader("Content-Type", "text/html")
                .withBody(productsHtml)));
    }

    @SuppressWarnings("unchecked")
    private void assertThatLoggerMessageIs(int invocations, String message) {
        verify(appender, times(invocations)).doAppend(argThat(new ArgumentMatcher() {
            @Override
            public boolean matches(Object argument) {
                return ((ILoggingEvent) argument).getFormattedMessage().equals(message);
            }
        }));
    }
}
