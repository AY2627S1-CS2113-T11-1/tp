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

    public void setStock(int stock) {
        this.stock = stock;
    }
}
