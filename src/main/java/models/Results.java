package models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Results {

    private UUID uuid = UUID.randomUUID();

    private List<Product> results;
    private Total total;

    public Results(List<Product> products, Total total) {
        this.results = products;
        this.total = total;

    }

    public List<Product> getResults() {
        return results;
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
        if (results != other.results) return false;
        if (total != other.total) return false;
        return true;
    }
}
