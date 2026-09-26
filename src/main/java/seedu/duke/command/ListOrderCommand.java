package seedu.duke.command;

import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Lists every order, oldest first, including cancelled ones.
 *
 * Corresponds to {@code order list}. Cancelled orders are shown with their status rather than
 * hidden, so order numbers in the list always run 1, 2, 3... without unexplained gaps.
 */
public class ListOrderCommand extends Command {
    @Override
    public void execute(ProductList products, OrderList orders, Ui ui) {
        ui.showOrders(orders.getAll());
    }
}
