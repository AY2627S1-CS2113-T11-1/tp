package seedu.duke.command;

import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.Stock;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Lists every stock entry as a table with Product and Qty columns.
 *
 * Corresponds to {@code stock list}.
 */
public class ListStockCommand extends Command {
    /** Records stock listing events for debugging. */
    private static final Logger logger = Logger.getLogger(ListStockCommand.class.getName());

    /**
     * Displays all current stock records in tabular format.
     *
     * @param products the product catalogue
     * @param orders   the order list
     * @param expenses the shared expense list
     * @param stocks   the stock list
     * @param ui       the user interface
     * @throws SystemException if an execution error occurs
     */
    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        assert products != null : "Product catalogue must not be null";
        assert stocks != null : "Stock list must not be null";
        assert ui != null : "Ui must not be null";
        assert stocks.getStocks() != null : "Stock entries collection must not be null";

        for (Stock stock : stocks.getStocks()) {
            Product product = products.findByName(stock.getProduct());
            if (product != null) {
                stock.setQuantity(product.getStock());
                stock.quantity = product.getStock();
            }
        }

        ui.showStocks(stocks.getStocks());
        logger.info("Displayed stock list containing " + stocks.getStocks().size() + " entry/entries");
    }
}
