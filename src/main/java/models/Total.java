package models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Total {

    private UUID uuid = UUID.randomUUID();

    private BigDecimal gross;
    private BigDecimal vat;

    public Total(List<Product> products) {
        BigDecimal totalUnitPrice = products.stream().map(Product::getUnitPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.gross = totalUnitPrice.setScale(2, RoundingMode.HALF_UP);
        this.vat = BigDecimal.valueOf(totalUnitPrice.doubleValue() - (totalUnitPrice.doubleValue() / 1.2)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGross() {
        return gross;
    }
    public BigDecimal getVat() {
        return vat;
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
        Total other = (Total) obj;
        if (!Objects.equals(gross, other.gross)) return false;
        if (!Objects.equals(vat, other.vat)) return false;
        return true;
    }
}
