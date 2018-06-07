package models;

import java.util.List;

public class Results {

    private List<SainsburysData> results;
    private Total total;

    public List<SainsburysData> getResults() {
        return results;
    }

    public void setResults(List<SainsburysData> results) {
        this.results = results;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }
}
