package seedu.duke;

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
     * Returns the total cost of the order, summed over all its lines.
     */
    public double getTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
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
