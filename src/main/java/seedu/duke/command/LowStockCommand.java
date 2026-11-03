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
     * Identifies catalogue products with quantity at or below the threshold, including products that
     * were never stocked, and displays them in alphabetical order.
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

        // The catalogue is checked rather than the stock list, because a product only gets a stock entry
        // after its first "stock add". A product that was never stocked has 0 units and is the most
        // urgent to restock, so it must not be skipped.
        boolean isCatalogueEmpty = products.size() == 0;
        List<Stock> lowStocks = new ArrayList<>();

        for (Product product : products.getSortedByName()) {
            if (product.getStock() > threshold) {
                continue;
            }
            Stock existing = stocks.findByProduct(product.getName());
            if (existing != null) {
                // Keep the stored entry in step with the catalogue before it is displayed.
                existing.setQuantity(product.getStock());
                lowStocks.add(existing);
            } else {
                // Display-only entry; it is not added to the stock list, so "stock list" is unchanged.
                lowStocks.add(new Stock(product.getName(), product.getPrice(), product.getStock()));
            }
        }

        logger.info("Found " + lowStocks.size() + " low stock items (threshold=" + threshold + ")");
        ui.showLowStock(lowStocks, threshold, isCatalogueEmpty);
    }
}

