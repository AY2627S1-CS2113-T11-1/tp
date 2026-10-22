package seedu.duke;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Represents a customer's purchase of one or more products.
 *
 * An order starts active and can be cancelled once. A cancelled order is kept rather than deleted,
 * so its ID is never reused and the sales record can still show that it existed.
 */
public class Order {
    private final int id;
    private String customer;
    private List<OrderItem> items;
    private boolean isCancelled;
    private boolean isPaid;
    private BigDecimal discountPercent = BigDecimal.ZERO;

    /**
     * Constructs an active order.
     *
     * @param id       the order number shown to the user, assigned by {@link OrderList}
     * @param customer the customer's name as typed by the user
     * @param items    the lines of the order; copied, so later changes to the caller's list have no effect
     */
    public Order(int id, String customer, List<OrderItem> items) {
        this.id = id;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.isCancelled = false;
    }

    public int getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Returns whether payment has been recorded for this order.
     *
     * @return true when the order has been paid
     */
    public boolean isPaid() {
        return isPaid;
    }

    /**
     * Marks an active, unpaid order as paid after the payment amount has been checked.
     *
     * @throws SystemException if the order is cancelled or already paid
     */
    public void markPaid() throws SystemException {
        if (isCancelled) {
            throw new SystemException("Cannot pay for cancelled order " + id + ".");
        }
        if (isPaid) {
            throw new SystemException("Order " + id + " has already been paid.");
        }
        isPaid = true;
    }

    /**
     * Returns the total cost of the order, summed over all its lines.
     * Returns the number of units bought, summed over all lines of the order.
     */
    public int getTotalUnits() {
        int units = 0;
        for (OrderItem item : items) {
            units += item.getQuantity();
        }
        return units;
    }

    /**
     * Replaces the customer and lines of this active order, keeping its order number.
     *
     * The caller ({@code EditOrderCommand}) is responsible for validating the new values and moving stock
     * before calling this, so that a rejected edit never leaves the order half-changed. The cancelled check
     * is repeated here because an edit to a cancelled order would bring a dead sale back into the figures.
     *
     * @param newCustomer the customer's name, never blank
     * @param newItems    the new lines of the order, at least one; copied, as in the constructor
     * @throws SystemException if the order has been cancelled
     */
    public void update(String newCustomer, List<OrderItem> newItems) throws SystemException {
        assert newCustomer != null && !newCustomer.isBlank() : "Edited customer name must be validated as non-blank";
        assert newItems != null && !newItems.isEmpty() : "An edited order must keep at least one line";

        if (isCancelled) {
            throw new SystemException("Order " + id + " is cancelled and cannot be edited.");
        }
        this.customer = newCustomer;
        this.items = List.copyOf(newItems);
    }

    /**
     * Returns the discount percentage, e.g. 12.5 for 12.5%, or zero if there is no discount.
     */
    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Returns true if a non-zero discount applies to this order.
     */
    public boolean hasDiscount() {
        return discountPercent.signum() > 0;
    }

    /**
     * Sets the discount percentage, replacing any previous discount. Zero removes the discount.
     *
     * Discounts are not added together: {@code d/10} followed by {@code d/5} leaves a 5% discount.
     *
     * @param percent the new percentage, already validated by the parser as 0 to 100 with at most two decimals
     * @throws SystemException if the order has been cancelled
     */
    public void setDiscountPercent(BigDecimal percent) throws SystemException {
        assert percent != null : "Discount percentage must not be null";
        assert percent.signum() >= 0 && percent.compareTo(BigDecimal.valueOf(100)) <= 0
                : "Parser must keep the discount percentage between 0 and 100";
        assert percent.stripTrailingZeros().scale() <= 2 : "Parser must reject more than two decimal places";

        if (isCancelled) {
            throw new SystemException("Order " + id + " is cancelled and cannot receive a discount.");
        }
        this.discountPercent = percent;
    }

    /**
     * Returns the undiscounted subtotal: each line's stored unit price times its quantity, summed.
     */
    public double getSubtotal() {
        return subtotalAmount().doubleValue();
    }

    /**
     * Returns the discount in dollars: subtotal times percentage, rounded to the nearest cent, with half a
     * cent rounded up.
     */
    public double getDiscountAmount() {
        return discountAmount().doubleValue();
    }

    /**
     * Returns the amount the customer pays: the subtotal minus the discount amount.
     */
    public double getTotal() {
        BigDecimal total = subtotalAmount().subtract(discountAmount());
        assert total.signum() >= 0 : "A discount of at most 100% can never make the total negative";
        return total.doubleValue();
    }

    /**
     * Returns the exact subtotal in dollars, using each line's stored unit price.
     *
     * {@code BigDecimal.valueOf(double)} goes through the shortest decimal form of the price, so a stored
     * price of 1.88 becomes exactly 1.88 rather than the nearest binary fraction.
     */
    private BigDecimal subtotalAmount() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : items) {
            subtotal = subtotal.add(BigDecimal.valueOf(item.getUnitPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the exact discount amount in dollars, already rounded to cents.
     */
    private BigDecimal discountAmount() {
        // movePointLeft(2) divides by 100 exactly, without the rounding questions of divide().
        return subtotalAmount().multiply(discountPercent).movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Marks this order as cancelled.
     *
     * The rule that an order can only be cancelled once lives here, next to the state it protects.
     *
     * @throws SystemException if the order is already cancelled
     */
    public void cancel() throws SystemException {
        if (isCancelled) {
            throw new SystemException("Order " + id + " is already cancelled.");
        }
        isCancelled = true;
    }
}
