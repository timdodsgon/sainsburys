package services;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import models.Product;
import models.Results;
import models.Total;
import org.mockito.ArgumentMatcher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class BaseTest {

    @SuppressWarnings("unchecked")
    protected void assertThatLoggerMessageIs(Appender appender, int invocations, String message) {
        verify(appender, times(invocations)).doAppend(argThat(new ArgumentMatcher() {
            @Override
            public boolean matches(Object argument) {
                return ((ILoggingEvent) argument).getFormattedMessage().equals(message);
            }
        }));
    }

    protected List<Product> givenListOfValidProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Sainsbury's Mixed Berry Twin Pack 200g", null, BigDecimal.valueOf(2.75), "Mixed Berries"));
        products.add(new Product("Sainsbury's Blueberries 200g", 45.0, BigDecimal.valueOf(1.75), "by Sainsbury's blueberries"));
        products.add(new Product("Sainsbury's Strawberries 400g", 33.0, BigDecimal.valueOf(1.75), "by Sainsbury's strawberries"));
        return products;
    }

    protected Results givenModelObjects() {
        List<Product> products = givenListOfValidProducts();
        return new Results(products, new Total(products));
    }
}
