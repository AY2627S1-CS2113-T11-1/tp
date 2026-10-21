package seedu.duke;

/**
 * Represents one product in the catalogue.
 *
 * A product is identified by its name. Its price is fixed when the product is created,
 * while its stock level changes as stock is received or sold, and always starts at zero.
 */
public class Product {
    private final String name;
    private final double price;
    private int stock;

    /**
     * Constructs a product with the given name and unit price, at zero stock.
     *
     * @param name  the product name as typed by the user, e.g. "Oat Milk"
     * @param price the unit price in dollars, e.g. 3.50
     */
    public Product(String name, double price) {
        // The parser rejects empty names and negative prices, so failing here means a caller skipped validation.
        assert name != null && !name.isBlank() : "Product name must be validated as non-empty before construction";
        assert price >= 0 : "Product price must be validated as non-negative before construction";

        this.name = name;
        this.price = price;
        this.stock = 0;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Returns the unit price formatted for display, always with two decimals, e.g. "$3.50".
     */
    public String getPriceString() {
        return String.format("$%.2f", price);
    }

    public int getStock() {
        return stock;
    }

    /**
     * Sets the number of units on hand.
     *
     * Callers (stock and order commands) must check quantities first, e.g. an order cannot take
     * more units than are in stock, so a negative value here is a bug in the caller.
     *
     * @param stock the new stock level, never negative
     */
    public void setStock(int stock) {
        assert stock >= 0 : "Stock cannot go negative; caller must check quantities before updating";
        this.stock = stock;
    }
}
