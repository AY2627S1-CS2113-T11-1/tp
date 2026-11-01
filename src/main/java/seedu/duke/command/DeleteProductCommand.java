package seedu.duke.command;

import java.util.logging.Logger;

import seedu.duke.ExpenseList;
import seedu.duke.Order;
import seedu.duke.OrderList;
import seedu.duke.Product;
import seedu.duke.ProductList;
import seedu.duke.StockList;
import seedu.duke.SystemException;
import seedu.duke.Ui;

/**
 * Removes a product from the catalogue, along with its stock record.
 *
 * Corresponds to {@code product delete p/PRODUCT}. A product that an active order still contains
 * cannot be deleted, because cancelling or editing that order later returns stock to the product.
 * Cancelled orders keep their own reference to the product, so their history still shows the
 * product's name after it is deleted.
 */
public class DeleteProductCommand extends Command {
    /**
     * Records catalogue changes for debugging, at FINE so they never appear in the user's CLI output.
     */
    private static final Logger logger = Logger.getLogger(DeleteProductCommand.class.getName());

    private final String name;

    /**
     * Constructs a command that deletes the product with the given name.
     *
     * @param name the product name, already validated as non-empty by the parser; matched ignoring case
     */
    public DeleteProductCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void execute(ProductList products, OrderList orders, ExpenseList expenses, StockList stocks, Ui ui)
            throws SystemException {
        Product product = products.findByName(name);
        if (product == null) {
            throw new SystemException("There is no product named \"" + name + "\" in the catalogue.");
        }

        Order activeOrder = orders.findActiveOrderWithProduct(product.getName());
        if (activeOrder != null) {
            logger.fine("Rejected product delete, still in active order " + activeOrder.getId() + ": " + name);
            throw new SystemException("Can't delete " + product.getName() + ", it's still in active order "
                    + activeOrder.getId() + ". Cancel the order first.");
        }

        products.remove(product);
        boolean hadStockEntry = stocks.removeByProduct(product.getName());
        logger.fine("Deleted product " + product.getName() + (hadStockEntry ? " and its stock entry" : "")
                + "; catalogue size is now " + products.size());
        ui.showProductDeleted(product);
    }
}
