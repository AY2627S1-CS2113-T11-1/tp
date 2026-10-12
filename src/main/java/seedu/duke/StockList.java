package seedu.duke;

import java.util.ArrayList;

public class StockList {
    private final ArrayList<Stock> stocks;

    public StockList() {
        this.stocks = new ArrayList<>();
    }

    public void add(Stock stock) {
        stocks.add(stock);
    }

    public Stock findByProduct(String productName) {
        for (Stock s : stocks) {
            if (s.product.equalsIgnoreCase(productName)) {
                return s;
            }
        }
        return null;
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }
}

