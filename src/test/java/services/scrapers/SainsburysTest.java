package services.scrapers;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import config.Config;
import models.Product;
import org.jsoup.HttpStatusException;
import org.junit.*;
import org.apache.commons.io.IOUtils;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import services.BaseTest;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class SainsburysTest extends BaseTest {

    private static final String URL = "http://127.0.0.1:8089/products/products.html";
    private static final String BASE_URL = "http://127.0.0.1:8089";
    private static final String MALFORMED_URL = "http://127.0.0.1:8089http/products/products.html";

    private Sainsburys sainsburys;
    private Properties properties;
    private Config config;

    @Mock
    private Appender appender;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws IOException {

        sainsburys = new Sainsburys();

        properties = new Properties();
        properties.setProperty("url", URL);
        properties.setProperty("baseurl", BASE_URL);
        config = new Config(properties);

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
    public void testScrapeReturnsExpectedListOfProducts() throws IOException {
        // given
        List<Product> expected = givenListOfValidProducts();
        // when
        List<Product> actual = sainsburys.scrape(config);
        // then
        assertThat(expected.size(), is(actual.size()));
        assertProductsInListsAreEqual(expected, actual);
        assertThatLoggerMessageIs(appender,1,"Starting scrape");
    }


    @Test
    public void testScrapeLogsNullProductAndContinues() throws IOException {
        // given
        mockHTMLResponse("/nullproduct.html", "/products/berrys.html", HttpsURLConnection.HTTP_OK);
        // when
        List<Product> actual = sainsburys.scrape(config);
        // then
        assertThat(actual.size(), is(2));
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
        sainsburys.getSainsburysProductLinks(new URL(config.getUrl()));
}

    @Test
    public void testGetSainsburysProductRedirectHTTPStatusThrowsHttpStatusException() throws IOException {
        // given
        mockHTMLResponse("/products.html", "/products/berrys.html",HttpsURLConnection.HTTP_MOVED_TEMP);
        // expected
        exception.expect(HttpStatusException.class);
        exception.expectMessage("Invalid response");
        // when
        sainsburys.getSainsburysProduct(new URL(config.getBaseURL() + "/products/berrys.html"), config);
    }

    @Test
    public void testMalformedURLThrowsMalformedURLException() throws IOException {
        // given
        properties = new Properties();
        properties.setProperty("url", MALFORMED_URL);
        properties.setProperty("baseurl", BASE_URL);
        config = new Config(properties);

        // expected
        exception.expect(MalformedURLException.class);
        exception.expectMessage("8089http");
        // when
        sainsburys.scrape(config);
    }

    @Test
    public void testGetSainsburysProductLinksReturnsCorrectNumberOfLinks() throws IOException {
        // when
        List<String> links = sainsburys.getSainsburysProductLinks(new URL(config.getUrl()));
        // then
        assertThat(3, is(links.size()));
    }

    @Test
    public void testGetSainsburysProductDataReturnsCorrectProductData() throws IOException {
        // when
        Product product = sainsburys.getSainsburysProduct(new URL(config.getBaseURL()+ "/products/berrys.html"), config);
        // then
        assertThat("Sainsbury's Mixed Berry Twin Pack 200g", is(product.getTitle()));
        assertThat(product.getkCalPer100g(), is(nullValue()));
        assertThat(BigDecimal.valueOf(2.75), is(product.getUnitPrice()));
        assertThat("Mixed Berries", is(product.getDescription()));
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

    private void assertProductsInListsAreEqual(List<Product> expected, List<Product> actual) {
        int i = 0;
        for (Product product : expected) {
            assertThat(product.equals(actual.get(i)), is(true));
            i++;
        }
    }
}
