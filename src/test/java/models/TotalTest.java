package models;


import org.junit.Test;
import services.BaseTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TotalTest extends BaseTest {

    @Test
    public void testTotalHasCorrectValues() throws IOException {
        // given
        List<Product> products = givenListOfValidProducts();
        // when
        Total total = new Total(products);
        // then
        assertThat(total.getGross(), is(BigDecimal.valueOf(6.25)));
        assertThat(total.getVat(), is(BigDecimal.valueOf(1.04)));
    }

    @Test
    public void testTotalHasCorrectValuesWithNULLUnitPrice() throws IOException {
        // given
        List<Product> products = givenListOfProductsWithNULLUnitPrice();
        // when
        Total total = new Total(products);
        // then
        assertThat(total.getGross(), is(BigDecimal.valueOf(4.50).setScale(2, RoundingMode.HALF_UP)));
        assertThat(total.getVat(), is(BigDecimal.valueOf(0.75).setScale(2, RoundingMode.HALF_UP)));
    }

    private List<Product> givenListOfProductsWithNULLUnitPrice() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Sainsbury's Mixed Berry Twin Pack 200g", null, BigDecimal.valueOf(2.75), "Mixed Berries"));
        products.add(new Product("Sainsbury's Blueberries 200g", 45.0, null, "by Sainsbury's blueberries"));
        products.add(new Product("Sainsbury's Strawberries 400g", 33.0, BigDecimal.valueOf(1.75), "by Sainsbury's strawberries"));
        return products;
    }
}
