package seedu.duke.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.Order;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.Ui;

/**
 * Displays each order belonging to the requested customer, including cancelled orders.
 */
public class OrderDetailsCommand extends Command {
    private static final Logger logger = Logger.getLogger(OrderDetailsCommand.class.getName());
    private final String customer;

    /**
     * Creates a details command with a validated customer name.
     *
     * @param customer full name of the customer
     */
    public OrderDetailsCommand(String customer) {
        assert customer != null && !customer.isBlank() : "Order details require a customer name";
        this.customer = customer.trim();
    }

    /**
     * Finds matching orders without changing their contents or status.
     */
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) {
        List<Order> matches = new ArrayList<>();
        for (Order order : orders.getAll()) {
            if (order.getCustomer().equalsIgnoreCase(customer)) {
                matches.add(order);
            }
        }
        logger.fine(() -> "Found " + matches.size() + " order(s) for order details");
        ui.showOrderDetails(customer, matches);
    }
}
