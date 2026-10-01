package seedu.duke.command;

import seedu.duke.OrderList;
import seedu.duke.ExpenseList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Lists every product in the catalogue in alphabetical order.
 *
 * Corresponds to {@code product list}.
 */
public class ListProductCommand extends Command {
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, Ui ui) {
        ui.showProducts(products.getSortedByName());
    }
}
