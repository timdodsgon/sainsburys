package services.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import models.SainsburysData;
import models.Total;
import org.json.JSONException;
import org.jsoup.HttpStatusException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.io.IOUtils;
import org.junit.rules.ExpectedException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ScraperServiceImplTest {

    private static final String BASE_URL = "http://127.0.0.1:8089";
    private static final String PATH = "/products/products.html";
    private static final String MALFORMED_URL = "http://127.0.0.1:8089http/products/products.html";

    private ScraperServiceImpl scraperService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws IOException {

        scraperService = new ScraperServiceImpl();

        /* Mock products list */
        String productsHtml = IOUtils.toString(this.getClass().getResourceAsStream("/products.html"), "UTF-8");
        givenWebSiteResponse(productsHtml, "/products/products.html", HttpsURLConnection.HTTP_OK);

        /* Mock individual product pages */
        String berrysHtml = IOUtils.toString(this.getClass().getResourceAsStream("/berrys.html"), "UTF-8");
        givenWebSiteResponse(berrysHtml, "/products/berrys.html", 200);

        String blueberriesHtml = IOUtils.toString(this.getClass().getResourceAsStream("/blueberries.html"), "UTF-8");
        givenWebSiteResponse(blueberriesHtml, "/products/blueberries.html", 200);

        String strawberriesHtml = IOUtils.toString(this.getClass().getResourceAsStream("/strawberries.html"), "UTF-8");
        givenWebSiteResponse(strawberriesHtml, "/products/strawberries.html", 200);
    }

    @Test
    public void testScrapingWebSiteReturnsExpectedJSON() throws IOException, JSONException {
        // given
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/expected.json"), "UTF-8");
        // when
        String actual = scraperService.scrape(BASE_URL, PATH);
        // then
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    public void testInvalidHTTPStatusThrowsHttpStatusException() throws IOException {
        // given
        String productsHtml = IOUtils.toString(this.getClass().getResourceAsStream("/products.html"), "UTF-8");
        givenWebSiteResponse(productsHtml, "/products/products.html",HttpsURLConnection.HTTP_INTERNAL_ERROR);
        // expected
        exception.expect(HttpStatusException.class);
        // when
        scraperService.scrape(BASE_URL, PATH);
    }

    @Test
    public void testMalformedURLThrowsMalformedURLException() throws IOException {
        // expected
        exception.expect(MalformedURLException.class);
        // when
        scraperService.scrape(MALFORMED_URL, PATH);
    }

    @Test
    public void testGetSainsburysProductLinksReturnsCorrectNumberOfLinks() throws IOException {
        // when
        List<String> links = scraperService.getSainsburysProductLinks(new URL(BASE_URL + PATH));
        // then
        assertEquals(3, links.size());
    }

    @Test
    public void testGetSainsburysProductDataReturnsCorrectProductData() throws IOException {
        // when
        SainsburysData product = scraperService.getSainsburysProductData(new URL(BASE_URL + "/products/berrys.html"));
        // then
        assertEquals("Sainsbury's Mixed Berry Twin Pack 200g", product.getTitle());
        assertNull(product.getkCalPer100g());
        assertEquals(new Double(2.75), product.getUnitPrice());
        assertEquals("Mixed Berries", product.getDescription());
    }

    @Test
    public void testPopulateTotalGeneratesCorrectVATAmount() throws IOException {
        // given
        Total total = new Total();
        double gross = 40;
        double expectedVAT = 6.67;
        // when
        scraperService.populateTotal(gross);
        // then
        assertEquals(BigDecimal.valueOf(expectedVAT), total.getVat());
    }

    private void givenWebSiteResponse(String productsHtml, String url, int httpStatus) {
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                .withStatus(httpStatus)
                .withHeader("Content-Type", "text/html")
                .withBody(productsHtml)));
    }
}
