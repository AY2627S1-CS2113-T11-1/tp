package seedu.duke;

/**
 * Represents one line of an order: a product, how many units were bought, and the unit price paid.
 *
 * The unit price is copied from the product when the order is created, so editing a product's
 * price later does not change what an existing order says the customer paid.
 */
public class OrderItem {
    private final Product product;
    private final int quantity;
    private final double unitPrice;

    /**
     * Constructs an order line for the given product and quantity at the product's current price.
     *
     * @param product  the product bought
     * @param quantity the number of units bought, already validated as positive
     */
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Returns the cost of this line, i.e. unit price times quantity.
     */
    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
