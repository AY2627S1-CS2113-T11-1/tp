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
 * Removes a specified quantity of stock from a product in the catalogue and updates the stock list.
 *
 * Corresponds to {@code stock remove p/PRODUCT [q/QUANTITY]}.
 */
public class RemoveStockCommand extends Command {
    /** Records stock removal events for debugging and auditing. */
    private static final Logger logger = Logger.getLogger(RemoveStockCommand.class.getName());

    private final String productName;
    private final int quantityToRemove;
    private final boolean defaultBehavior;

    /**
     * Constructs a command that removes a given quantity of stock.
     *
     * @param productName      the name of the product, validated as non-empty by the parser
     * @param quantityToRemove the quantity to deduct, validated as positive
     * @param defaultBehavior  true if quantity was omitted and defaulted to 1 unit
     */
    public RemoveStockCommand(String productName, int quantityToRemove, boolean defaultBehavior) {
        assert productName != null && !productName.isBlank()
                : "Parser must supply a non-empty product name";
        assert quantityToRemove > 0 : "Quantity to remove must be positive";

        this.productName = productName;
        this.quantityToRemove = quantityToRemove;
        this.defaultBehavior = defaultBehavior;
    }

    /**
     * Executes the stock removal against the product catalogue and stock list.
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

        Product product = products.findByName(productName);
        Stock existing = stocks.findByProduct(productName);
        if (product == null && existing == null) {
            logger.warning("Stock removal rejected: product \"" + productName
                    + "\" not found in catalogue or stock list");
            ui.showStockNotFound();
            return;
        }

        int currentStock = product != null ? product.getStock() : existing.getQuantity();
        int newStock = Math.max(0, currentStock - quantityToRemove);
        assert newStock >= 0 : "Calculated stock after removal must not be negative";

        if (product != null) {
            product.setStock(newStock);
        }
        if (existing != null) {
            existing.setQuantity(newStock);
        }

        logger.info("Removed " + quantityToRemove + " unit(s) of \"" + productName
                + "\", remaining stock: " + newStock);
        ui.showStockRemoved(productName, quantityToRemove, defaultBehavior);
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantityToRemove() {
        return quantityToRemove;
    }
}
