package services.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import ch.qos.logback.classic.Logger;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.LoggerFactory;
import services.BaseScaperServiceTest;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SainsburysTest  extends BaseScaperServiceTest {

    private static final String BASE_URL = "http://127.0.0.1:8089";
    private static final String PATH = "/products/products.html";
    private static final String MALFORMED_URL = "http://127.0.0.1:8089http/products/products.html";

    private Sainsburys sainsburys;

    @Mock
    private Appender appender;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws IOException {

        sainsburys = new Sainsburys();

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(appender);

        when(appender.getName()).thenReturn("MOCK");
        when(appender.isStarted()).thenReturn(true);

        /* Mock products list */
        mockHTMLResponse("/products.html", "/products/products.html", HttpsURLConnection.HTTP_OK);

        /* Mock individual product pages */
        mockHTMLResponse("/berrys.html", "/products/berrys.html", HttpsURLConnection.HTTP_OK);
        mockHTMLResponse("/blueberries.html", "/products/blueberries.html", HttpsURLConnection.HTTP_OK);
        mockHTMLResponse("/strawberries.html", "/products/strawberries.html", HttpsURLConnection.HTTP_OK);
    }

    @Test
    public void testScrapeReturnsExpectedJSON() throws IOException, JSONException {
        // given
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/expected.json"), "UTF-8");
        // when
        String actual = sainsburys.scrape(BASE_URL, PATH);
        // then
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
        assertThatLoggerMessageIs(appender,1,"Starting scrape");
    }

    @Test
    public void testScrapeLogsNullProductAndContinues() throws IOException, JSONException {
        // given
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/expected-null-product.json"), "UTF-8");
        mockHTMLResponse("/nullproduct.html", "/products/berrys.html",HttpsURLConnection.HTTP_OK);
        // when
        String actual = sainsburys.scrape(BASE_URL, PATH);
        // then
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
        assertThatLoggerMessageIs(appender,1,"Starting scrape");
        assertThatLoggerMessageIs(appender,1,"The following link /products/berrys.html returned no product data");
    }

    @Test
    public void testGetSainsburysProductLinksRedirectHTTPStatusThrowsHttpStatusException() throws IOException {
        // given
        mockHTMLResponse("/berrys.html", "/products/products.html",HttpsURLConnection.HTTP_MOVED_TEMP);
        // expected
        exception.expect(HttpStatusException.class);
        exception.expectMessage("Invalid response");
        // when
        sainsburys.getSainsburysProductLinks(new URL(BASE_URL + PATH));
    }

    @Test
    public void testGetSainsburysProductRedirectHTTPStatusThrowsHttpStatusException() throws IOException {
        // given
        mockHTMLResponse("/products.html", "/products/berrys.html",HttpsURLConnection.HTTP_MOVED_TEMP);
        // expected
        exception.expect(HttpStatusException.class);
        exception.expectMessage("Invalid response");
        // when
        sainsburys.getSainsburysProduct(new URL(BASE_URL + "/products/berrys.html"));
    }

    @Test
    public void testMalformedURLThrowsMalformedURLException() throws IOException {
        // expected
        exception.expect(MalformedURLException.class);
        exception.expectMessage("8089http");
        // when
        sainsburys.scrape(MALFORMED_URL, PATH);
    }

    @Test
    public void testThrowsJsonProcessingException() throws IOException {
        // given
        ObjectMapper spyObjectMapper = Mockito.spy(new ObjectMapper());
        when(spyObjectMapper.writeValueAsString(any(Object.class))).thenThrow(new JsonProcessingException("Error"){});
        sainsburys.setObjectMapper(spyObjectMapper);
        // expected
        exception.expect(JsonProcessingException.class);
        exception.expectMessage("Error");
        // when
        sainsburys.scrape(BASE_URL, PATH);
    }

    @Test
    public void testGetSainsburysProductLinksReturnsCorrectNumberOfLinks() throws IOException {
        // when
        List<String> links = sainsburys.getSainsburysProductLinks(new URL(BASE_URL + PATH));
        // then
        assertThat(3, is(links.size()));
    }

    @Test
    public void testGetSainsburysProductDataReturnsCorrectProductData() throws IOException {
        // when
        Product product = sainsburys.getSainsburysProduct(new URL(BASE_URL + "/products/berrys.html"));
        // then
        assertThat("Sainsbury's Mixed Berry Twin Pack 200g", is(product.getTitle()));
        assertThat(product.getkCalPer100g(), is(nullValue()));
        assertThat(2.75, is(product.getUnitPrice()));
        assertThat("Mixed Berries", is(product.getDescription()));
    }

    @Test
    public void testCalculateVATFromRunningTotalGeneratesCorrectVATAmount() throws IOException {
        // given
        double runningTotal = 40;
        double expectedVAT = 6.67;
        // when
        Total total = sainsburys.calculateVATFromRunningTotal(runningTotal);
        // then
        assertThat(BigDecimal.valueOf(expectedVAT), is(total.getVat()));
    }

    @Test
    public void testisJSONValidPassesWithValidJSON() {
        boolean isJSONValid = sainsburys.isJSONValid("{}");
        assertThat(isJSONValid, is(true));
    }

    @Test
    public void testisJSONValidFailsWithInValidJSON() {
        boolean isJSONValid = sainsburys.isJSONValid("");
        assertThat(isJSONValid, is(false));
    }

    private void mockHTMLResponse(String s, String s2, int httpOk) throws IOException {
        String productsHtml = IOUtils.toString(this.getClass().getResourceAsStream(s), "UTF-8");
        givenWebSiteResponse(productsHtml, s2, httpOk);
    }

    private void givenWebSiteResponse(String productsHtml, String url, int httpStatus) {
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(httpStatus)
                        .withHeader("Content-Type", "text/html")
                        .withBody(productsHtml)));
    }
}
