package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Model to hold product data
 */
@JsonPropertyOrder({"title", "kCalPer100g", "unitPrice", "description"})
public class Product {

    private UUID uuid = UUID.randomUUID();

    private String title;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("kcal_per_100g")
    private Double kCalPer100g;
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    private String description;

    public Product(String title, Double kCalPer100g, BigDecimal unitPrice, String description) {
        this.title = title;
        this.kCalPer100g = kCalPer100g;
        this.unitPrice = unitPrice;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public Double getkCalPer100g() { return kCalPer100g; }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Product other = (Product) obj;
        if (title != other.title) return false;
        if (kCalPer100g != other.kCalPer100g) return false;
        if (unitPrice != other.unitPrice) return false;
        if (description != other.description) return false;
        return true;
    }
}
