package seedu.duke.command;

import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.Ui;

/**
 * Ends the session.
 *
 * Corresponds to {@code bye}.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(ProductList products, OrderList orders, Ui ui) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
