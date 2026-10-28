package seedu.duke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores and manages inventory stock records in memory.
 */
public class StockList {
    private final ArrayList<Stock> stocks;

    /**
     * Constructs an empty stock list.
     */
    public StockList() {
        this.stocks = new ArrayList<>();
    }

    /**
     * Adds a stock entry to the stock list.
     *
     * @param stock the stock entry to add
     */
    public void add(Stock stock) {
        assert stock != null : "Stock entry cannot be null";
        stocks.add(stock);
    }

    /**
     * Finds a stock entry by its product name (case-insensitive).
     *
     * @param productName the product name to search for
     * @return the matching stock entry, or null if not found
     */
    public Stock findByProduct(String productName) {
        assert productName != null : "Product name to search cannot be null";
        for (Stock s : stocks) {
            if (s.getProduct().equalsIgnoreCase(productName.trim())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Removes the stock entry for the given product name (case-insensitive), if there is one.
     *
     * A product only gets a stock entry once stock is first added, so a product that never had
     * stock has nothing to remove.
     *
     * @param productName the product whose stock entry should be removed
     * @return true if an entry was removed, false if the product had no stock entry
     */
    public boolean removeByProduct(String productName) {
        Stock stock = findByProduct(productName);
        if (stock == null) {
            return false;
        }
        return stocks.remove(stock);
    }

    /**
     * Returns an unmodifiable view of the stock list to protect internal state.
     *
     * @return unmodifiable list of stocks
     */
    public List<Stock> getStocks() {
        return Collections.unmodifiableList(stocks);
    }

    /**
     * Returns the number of stock entries in the list.
     */
    public int size() {
        return stocks.size();
    }

    /**
     * Returns true if there are no stock entries.
     */
    public boolean isEmpty() {
        return stocks.isEmpty();
    }
}
