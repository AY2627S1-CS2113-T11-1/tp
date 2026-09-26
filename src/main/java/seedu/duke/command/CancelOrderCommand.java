package seedu.duke.command;

import seedu.duke.Order;
import seedu.duke.OrderItem;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Cancels an active order and puts its quantities back into stock.
 *
 * Corresponds to {@code order cancel ORDER_NUMBER}. The order stays in the order list, marked as
 * cancelled, so its number is not reused and sales reporting can leave its total out.
 */
public class CancelOrderCommand extends Command {
    private final int orderId;

    /**
     * Constructs a command that cancels the order with the given number.
     *
     * @param orderId the order number, already validated as a positive integer by the parser
     */
    public CancelOrderCommand(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public void execute(ProductList products, OrderList orders, Ui ui) throws SystemException {
        Order order = orders.findById(orderId);
        if (order == null) {
            throw new SystemException("    There is no order " + orderId + ".");
        }

        // Cancel first: if the order was already cancelled this throws before stock is restored twice.
        order.cancel();
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }
        ui.showOrderCancelled(order);
    }
}
