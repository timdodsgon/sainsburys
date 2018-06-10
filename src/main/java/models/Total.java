package models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Total {

    private UUID uuid = UUID.randomUUID();

    private BigDecimal gross;
    private BigDecimal vat;

    public Total(BigDecimal productsTotal, BigDecimal inclusiveVAT) {
        this.gross = productsTotal;
        this.vat = inclusiveVAT;
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
        if (gross != other.gross) return false;
        if (vat != other.vat) return false;
        return true;
    }
}
