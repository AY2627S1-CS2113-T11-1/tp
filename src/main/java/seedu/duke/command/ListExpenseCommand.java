package seedu.duke.command;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Lists all operating expenses in insertion order.
 *
 * Corresponds to {@code expense list}.
 */
public class ListExpenseCommand extends Command {
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, Ui ui) {
        ui.showExpenses(expenses.getExpenses());
    }
}
