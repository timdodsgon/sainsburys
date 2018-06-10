package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Model to hold product data
 */
@JsonPropertyOrder({"title", "kCalPer100g", "unitPrice", "description"})
public class Product {

    private String title;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("kcal_per_100g")
    private Double kCalPer100g;
    @JsonProperty("unit_price")
    private Double unitPrice;
    private String description;

    public Product(String title, Double kCalPer100g, Double unitPrice, String description) {
        this.title = title;
        this.kCalPer100g = kCalPer100g;
        this.unitPrice = unitPrice;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public Double getkCalPer100g() {
        return kCalPer100g;
    }
    public Double getUnitPrice() {
        return unitPrice;
    }
    public String getDescription() {
        return description;
    }
}
