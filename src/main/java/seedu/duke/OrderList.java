package seedu.duke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores every order in memory and hands out order numbers.
 *
 * Order numbers start at 1 and only ever go up, so a number always refers to the same order,
 * even after that order is cancelled.
 */
public class OrderList {
    private final ArrayList<Order> orders = new ArrayList<>();
    private int nextId = 1;

    /**
     * Creates an order with the next free order number, stores it, and returns it.
     *
     * @param customer the customer's name
     * @param items    the lines of the order
     */
    public Order create(String customer, List<OrderItem> items) {
        Order order = new Order(nextId, customer, items);
        nextId++;
        orders.add(order);
        return order;
    }

    /**
     * Returns the order with the given number, or null if there is none.
     *
     * @param id the order number to look for
     */
    public Order findById(int id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    /**
     * Returns every order, oldest first, including cancelled ones.
     *
     * The list is read-only, so callers cannot add or remove orders except through this class.
     */
    public List<Order> getAll() {
        return Collections.unmodifiableList(orders);
    }

    /**
     * Returns orders that still count as confirmed sales.
     *
     * Cancelled orders remain in {@link #getAll()} for order-history purposes, but are not included
     * in sales figures because their amount has been excluded from recorded sales.
     */
    public List<Order> getConfirmedSales() {
        List<Order> confirmedSales = new ArrayList<>();
        for (Order order : orders) {
            if (!order.isCancelled()) {
                confirmedSales.add(order);
            }
        }
        return Collections.unmodifiableList(confirmedSales);
    }

    public int size() {
        return orders.size();
    }
}
