package models;

import java.math.BigDecimal;

public class Total {

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
}
