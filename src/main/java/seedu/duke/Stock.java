package seedu.duke;

import java.math.BigDecimal;

public class Stock {
    public String product;
    public BigDecimal price;
    public int quantity;

    public Stock(String product, BigDecimal price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    /** Convenience boundary overload for callers that still provide a decimal literal. */
    public Stock(String product, double price, int quantity) {
        this(product, BigDecimal.valueOf(price), quantity);
    }
}
