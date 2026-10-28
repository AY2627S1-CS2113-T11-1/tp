package seedu.duke.command;

import seedu.duke.StockList;
import seedu.duke.OrderList;
import seedu.duke.ExpenseList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Ends the session.
 *
 * Corresponds to {@code exit}.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
