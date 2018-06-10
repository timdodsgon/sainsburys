package models;

import java.util.List;

public class Results {

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
}
