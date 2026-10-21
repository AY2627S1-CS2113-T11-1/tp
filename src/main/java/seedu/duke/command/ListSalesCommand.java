package seedu.duke.command;

import java.util.List;
import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.Order;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.Ui;

/**
 * Lists every confirmed sale, excluding cancelled orders.
 *
 * Corresponds to {@code sales list}.
 */
public class ListSalesCommand extends Command {
    private static final Logger logger = Logger.getLogger(ListSalesCommand.class.getName());

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) {
        assert orders != null : "Sales listing requires the shared order history";
        List<Order> sales = orders.getConfirmedSales();
        assert sales.stream().noneMatch(Order::isCancelled) : "Confirmed sales must exclude cancelled orders";
        ui.showSales(sales);
        logger.fine(() -> "Displayed " + sales.size() + " confirmed sale(s)");
    }
}
