package config;

/**
 * Config, used to hold application default values
 */
public final class Config {

    private Config() {
        // restrict instantiation
    }

    public static final String URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
    public static final String BASE_URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/";
    public static final String TITLE_CSS_SELECTOR = "div.productTitleDescriptionContainer h1";
    public static final String CALORIES_CSS_SELECTOR= "td.nutritionLevel1, td:eq(0)[class], tr:eq(1) td:eq(1)";
    public static final String UNIT_PRICE_CSS_SELECTOR = "p.pricePerUnit";
    public static final String DESCRIPTION_CSS_SELECTOR = "div.productText p";
}
