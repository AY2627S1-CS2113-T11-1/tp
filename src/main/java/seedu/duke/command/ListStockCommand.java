package seedu.duke.command;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Lists every stock entry as a table with Product and Qty columns.
 *
 * Corresponds to {@code stock list}.
 */
public class ListStockCommand extends Command {

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui) 
            throws SystemException {
        ui.showStocks(stocks.getStocks());
    }
}

