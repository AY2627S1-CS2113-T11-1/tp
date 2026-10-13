package seedu.duke;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Selects a customer's confirmed purchases for individual receipts without changing order history.
 * The current sales model records payment when an order is created; cancelled orders are excluded.
 */
public class ReceiptGenerator {
    private static final Logger logger = Logger.getLogger(ReceiptGenerator.class.getName());

    /**
     * Returns eligible orders, oldest first, matching the full customer name without case sensitivity.
     * Customers with only cancelled orders are known customers, but have no receipts.
     *
     * @param orders the existing order history, including cancelled orders
     * @param customer the full customer name to look up
     * @throws SystemException if the customer has never placed an order
     */
    public List<Order> generate(OrderList orders, String customer) throws SystemException {
        // These are caller contracts; user input is validated by the receipt parser.
        assert orders != null : "Receipt generation requires the shared order history";
        assert customer != null && !customer.isBlank() : "Receipt generation requires a validated customer name";
        boolean customerExists = false;
        List<Order> receipts = new ArrayList<>();
        for (Order order : orders.getAll()) {
            if (order.getCustomer().equalsIgnoreCase(customer.trim())) {
                customerExists = true;
                if (!order.isCancelled()) {
                    receipts.add(order);
                }
            }
        }
        if (!customerExists) {
            logger.fine("Receipt generation rejected: customer not found in order history");
            throw new SystemException("Customer not in database.");
        }
        logger.fine(() -> "Generated " + receipts.size() + " receipt(s)");
        return List.copyOf(receipts);
    }
}
