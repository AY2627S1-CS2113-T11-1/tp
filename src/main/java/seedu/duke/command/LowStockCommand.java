package seedu.duke.command;

import java.util.ArrayList;
import java.util.List;
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
 * Lists products whose current stock quantity is at or below a specified threshold.
 *
 * Corresponds to {@code stock low}.
 */
public class LowStockCommand extends Command {
    public static final int DEFAULT_THRESHOLD = 5;

    /** Records low stock inspection events for debugging. */
    private static final Logger logger = Logger.getLogger(LowStockCommand.class.getName());

    private final int threshold;

    /**
     * Creates a command to check for low stock using the default threshold of 5.
     */
    public LowStockCommand() {
        this(DEFAULT_THRESHOLD);
    }

    /**
     * Creates a command to check for low stock using a specified threshold.
     *
     * @param threshold the maximum stock quantity to consider as low stock
     */
    public LowStockCommand(int threshold) {
        assert threshold >= 0 : "Threshold must be non-negative";
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    /**
     * Identifies products in stock with quantity at or below the threshold and displays them.
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

        logger.info("Executing LowStockCommand with threshold=" + threshold);

        List<Stock> allStocks = stocks.getStocks();
        boolean isStockListEmpty = allStocks.isEmpty();
        List<Stock> lowStocks = new ArrayList<>();

        for (Stock stock : allStocks) {
            Product product = products.findByName(stock.product);
            if (product != null) {
                stock.quantity = product.getStock();
            }
            if (stock.quantity <= threshold) {
                lowStocks.add(stock);
            }
        }

        logger.info("Found " + lowStocks.size() + " low stock items (threshold=" + threshold + ")");
        ui.showLowStock(lowStocks, threshold, isStockListEmpty);
    }
}

