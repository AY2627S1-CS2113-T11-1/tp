package seedu.duke.command;

import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Lists every confirmed sale, excluding cancelled orders.
 *
 * Corresponds to {@code sales list}.
 */
public class ListSalesCommand extends Command {
    @Override
    public void execute(ProductList products, OrderList orders, Ui ui) {
        ui.showSales(orders.getConfirmedSales());
    }
}
