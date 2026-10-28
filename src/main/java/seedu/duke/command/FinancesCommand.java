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
 * Displays the total order value, payments received, expenses and resulting profit or loss.
 *
 * Corresponds to {@code finances}.
 */
public class FinancesCommand extends Command {
    private static final Logger logger = Logger.getLogger(FinancesCommand.class.getName());

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) {
        assert orders != null : "Financial report requires the shared order history";
        assert expenses != null : "Financial report requires the shared expense list";
        assert ui != null : "UI must be available for the financial report";
        List<Order> allOrders = orders.getAll();
        List<Order> paidSales = orders.getPaidSales();
        ui.showFinances(allOrders, paidSales, expenses.getExpenses());
        logger.fine(() -> "Displayed financial report for " + allOrders.size()
                + " order(s) and " + expenses.size() + " expense(s)");
    }
}
