package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * Model to hold product data scraped from Sainsbury's website
 */
public class SainsburysData {

    private String title;
    @JsonInclude(Include.NON_NULL)
    private Double kCalPer100g;
    private Double unitPrice;
    private String description;

    public SainsburysData(String title, Double kCalPer100g, Double unitPrice, String description) {
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

    public Double getkCalPer100g() {
        return kCalPer100g;
    }

    public void setkCalPer100g(Double kCalPer100g) {
        this.kCalPer100g = kCalPer100g;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
