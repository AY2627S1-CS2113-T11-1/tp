package seedu.duke;

/**
 * Represents one operating expense recorded for a product.
 */
public class Expense {
    private final String productName;
    private final double amount;

    /**
     * Creates an expense.
     *
     * @param productName the product associated with the expense
     * @param amount the expense amount in dollars
     */
    public Expense(String productName, double amount) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Expense amount must be a non-negative number.");
        }

        this.productName = productName.trim();
        this.amount = amount;
    }

    public String getProductName() {
        return productName;
    }

    public double getAmount() {
        return amount;
    }
}
