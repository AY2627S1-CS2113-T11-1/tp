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
        this(product, quantity, product.getPrice());
    }

    /**
     * Constructs an order line at a given unit price instead of the product's current price.
     *
     * Used when an order is edited but keeps the same product: the customer keeps the price
     * stored when the order was placed, even if the catalogue price has changed since.
     *
     * @param product   the product bought
     * @param quantity  the number of units bought, already validated as positive
     * @param unitPrice the unit price to record, never negative
     */
    public OrderItem(Product product, int quantity, double unitPrice) {
        assert product != null : "An order line must refer to a product";
        assert quantity > 0 : "Order line quantity must be validated as positive before construction";
        assert unitPrice >= 0 : "Order line unit price cannot be negative";

        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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
