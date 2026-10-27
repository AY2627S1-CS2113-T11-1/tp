package seedu.duke;

import java.math.BigDecimal;

/**
 * Represents one operating expense recorded for a product.
 */
public class Expense {
    private final String productName;
    private final BigDecimal amount;

    /**
     * Creates an expense.
     *
     * @param productName the product associated with the expense
     * @param amount the expense amount in dollars
     */
    public Expense(String productName, BigDecimal amount) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (amount == null || amount.signum() < 0 || amount.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Expense amount must be a non-negative number.");
        }

        this.productName = productName.trim();
        this.amount = amount.setScale(2);
    }

    /** Convenience boundary overload for callers that still provide a decimal literal. */
    public Expense(String productName, double amount) {
        this(productName, BigDecimal.valueOf(amount));
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
