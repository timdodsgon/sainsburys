package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@JsonPropertyOrder({"results", "total"})
public class Results {

    private UUID uuid = UUID.randomUUID();

    @JsonProperty("results")
    private List<Product> products;
    private Total total;

    public Results(List<Product> products, Total total) {
        this.products = products;
        this.total = total;

    }

    public List<Product> getProducts() {
        return products;
    }

    public Total getTotal() { return total; }

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
        Results other = (Results) obj;
        if (!Objects.equals(products, other.products)) return false;
        if (!Objects.equals(total, other.total)) return false;
        return true;
    }
}
