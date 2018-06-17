package config;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * Config, used to hold application default values
 */
public final class Config {

    private String url = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
    private String baseURL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/";
    private String titleCSSSelector = "div.productTitleDescriptionContainer h1";
    private String caloriesCSSSelector= "td.nutritionLevel1, td:eq(0)[class], tr:eq(1) td:eq(1)";
    private String priceCSSSelector = "p.pricePerUnit";
    private String descriptionCSSSelector = "div.productText p, div.itemTypeGroup";
    private String returnType = "JSON";
    private String scraper = "SAINSBURYS";

    public Config() {}

    public Config(final Properties properties) {
        if(StringUtils.isNotEmpty(properties.getProperty("url"))) this.url = properties.getProperty("url");
        if(StringUtils.isNotEmpty(properties.getProperty("baseurl"))) this.baseURL = properties.getProperty("baseurl");
        if(StringUtils.isNotEmpty(properties.getProperty("title"))) this.titleCSSSelector = properties.getProperty("title");
        if(StringUtils.isNotEmpty(properties.getProperty("calories"))) this.caloriesCSSSelector= properties.getProperty("calories");
        if(StringUtils.isNotEmpty(properties.getProperty("price"))) this.priceCSSSelector = properties.getProperty("price");
        if(StringUtils.isNotEmpty(properties.getProperty("description"))) this.descriptionCSSSelector = properties.getProperty("description");
        if (StringUtils.isNotEmpty(properties.getProperty("returntype")))
            this.returnType = properties.getProperty("returntype");
        if (StringUtils.isNotEmpty(properties.getProperty("scraper"))) this.scraper = properties.getProperty("scraper");

    }

    public String getUrl() { return url;}
    public String getBaseURL() { return baseURL; }
    public String getTitleCSSSelector() { return titleCSSSelector; }
    public String getCaloriesCSSSelector() { return caloriesCSSSelector; }
    public String getPriceCSSSelector() { return priceCSSSelector; }
    public String getDescriptionCSSSelector() { return descriptionCSSSelector; }

    public String getReturnType() {
        return returnType;
    }

    public String getScraper() {
        return scraper;
    }
}
