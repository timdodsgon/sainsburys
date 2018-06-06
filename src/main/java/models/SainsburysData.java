package models;

/**
 * Model to hold product data scraped from Sainsbury's website
 */
public class SainsburysData {

    private String title;
    private String kCalPer100g;
    private String unitPrice;
    private String description;

    public SainsburysData(String title, String kCalPer100g, String unitPrice, String description) {
        this.title = title;
        this.kCalPer100g = kCalPer100g;
        this.unitPrice = unitPrice;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getkCalPer100g() {
        return kCalPer100g;
    }

    public void setkCalPer100g(String kCalPer100g) {
        this.kCalPer100g = kCalPer100g;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
