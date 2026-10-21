package seedu.duke;

/**
 * Represents stock information for a product, tracking the product name, unit price, and available quantity.
 */
public class Stock {
    // Package-private fields preserve backwards compatibility within seedu.duke (such as Ui.java)
    String product;
    double price;
    int quantity;

    private final Product productRef;

    /**
     * Constructs a stock entry for a known product with a given initial quantity.
     *
     * @param product the catalogue product
     * @param quantity the initial stock quantity
     */
    public Stock(Product product, int quantity) {
        assert product != null : "Product cannot be null";
        assert quantity >= 0 : "Quantity must be non-negative";
        this.productRef = product;
        this.product = product.getName();
        this.price = product.getPrice();
        this.quantity = quantity;
        product.setStock(quantity);
    }

    /**
     * Constructs a stock entry from name, price, and quantity.
     *
     * @param product the product name
     * @param price the product price
     * @param quantity the stock quantity
     */
    public Stock(String product, double price, int quantity) {
        assert product != null && !product.trim().isEmpty() : "Product name cannot be empty";
        assert price >= 0 : "Price cannot be negative";
        assert quantity >= 0 : "Quantity must be non-negative";
        this.productRef = null;
        this.product = product.trim();
        this.price = price;
        this.quantity = quantity;
    }

    public String getProduct() {
        return productRef != null ? productRef.getName() : product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        assert price >= 0 : "Price cannot be negative";
        this.price = price;
    }

    public int getQuantity() {
        return productRef != null ? productRef.getStock() : quantity;
    }

    public void setQuantity(int quantity) {
        assert quantity >= 0 : "Quantity must be non-negative";
        this.quantity = quantity;
        if (productRef != null) {
            productRef.setStock(quantity);
        }
    }
}
